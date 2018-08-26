package com.kaybo.slot.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaybo.slot.AppException;
import com.kaybo.slot.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SlotController {

    private static Log logger = LogFactory.getLog(SlotController.class);

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @Value("${platform.url}")
    private String platformUrl;
    @Value("${item.url}")
    private String itemUrl;

    @GetMapping("/game/{gameNo}/playspin")
    @ResponseBody
    public Pool playspin(@RequestHeader(value="userNo") String userNo,
                         @RequestHeader(value="userKey") String userKey,
                         @PathVariable String gameNo,
                         HttpServletRequest httpServletRequest) throws IOException {


        int spinCount = getUserSpinCount(userNo,userKey,gameNo);


        GameItem gameItem = new GameItem();
        gameItem.setCash(100);
        gameItem.setItemId("1");
        gameItem.setItemName("spin100");
        gameItem.setIpAddr(httpServletRequest.getRemoteAddr());

        ObjectMapper mapper = new ObjectMapper();
        GameData gameData = new GameData();
        gameData.setSpinCount(spinCount + 1);
        gameItem.setGameData(mapper.writeValueAsString(gameData));

        HttpHeaders headers = new HttpHeaders();
        headers.add("userNo", userNo);
        headers.add("userKey", userKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> httpEntity = new HttpEntity<Object>(gameItem, headers);

        ResponseEntity<String> response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/useCash", HttpMethod.POST, httpEntity, String.class);

        if(response.getStatusCode() != HttpStatus.OK){
            throw new AppException(-9988, "Bad Request");
        }
        // spin 결과값 보여주기

        int count = sqlSessionTemplate.selectOne("slot.getSpinCount");

        Pool pool = sqlSessionTemplate.selectOne("slot.getShuffle", count);

        if(pool.getPaytableId() > 0){
            Paytable paytable = sqlSessionTemplate.selectOne("slot.getPaytable", pool.getPaytableId());
            pool.setMultiplier(paytable.getMultiplier());
            //todo 캐쉬지급 해야함
            //100 * multiplier
            getCash(userNo, userKey, gameNo, 100 * paytable.getMultiplier(), httpServletRequest.getRemoteAddr());

        }

        Reward reward = sqlSessionTemplate.selectOne("slot.selectRewardByCondition", spinCount+1);
        if(reward != null){

            History history = new History();
            history.setUserNo(userNo);
            history.setId(reward.getId());
            sqlSessionTemplate.insert("slot.insertHistory", history);

            pool.setReward(reward);

        }

        return pool;
    }


    @GetMapping("/game/{gameNo}/history")
    @ResponseBody
    public GameData history(@RequestHeader(value="userNo") String userNo,
                         @RequestHeader(value="userKey") String userKey,
                         @PathVariable String gameNo) throws IOException {


        GameData gameData = new GameData();

        int spinCount = getUserSpinCount(userNo,userKey,gameNo) + 1;
        gameData.setSpinCount(spinCount);

        List<History> rewards = sqlSessionTemplate.selectList("slot.listHistory", userNo);
        gameData.setRewards(rewards);

        return gameData;
    }


    @GetMapping("/game/{gameNo}/payment")
    @ResponseBody
    public void paymentAll(@RequestHeader(value="userNo") String userNo,
                        @RequestHeader(value="userKey") String userKey,
                        @PathVariable String gameNo,
                           HttpServletRequest httpServletRequest) throws IOException {

        int count = 0;

        List<History> historyList = sqlSessionTemplate.selectList("slot.listHistory", userNo);
        for(History history : historyList){
            if(history.getAchievedAt() != -1 && history.getPaidAt() == -1){
                count++;
                if(history.getType() == 1){
                    getCash(userNo, userKey, gameNo, history.getCount(), httpServletRequest.getRemoteAddr());

                }else{

                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("userNo", userNo);
                    headers.add("userKey", userKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    ItemRequest itemRequest  = new ItemRequest();
                    itemRequest.setCpCode("SLOT");
                    itemRequest.setGameCode("SF");
                    itemRequest.setUserUid(Integer.parseInt(userNo));
                    itemRequest.setItemId(history.getType());

                    HttpEntity<?> httpEntity = new HttpEntity<Object>(itemRequest, headers);
                    ResponseEntity<String> response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/getItem", HttpMethod.POST, httpEntity, String.class);


                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(response.getBody());

                    JsonNode message = actualObj.get("message");

                    String result = message.get("result").toString();
                    String result_message = message.get("result_msg").toString();

                    if(!result.equals("200")){
                        throw new AppException(Integer.parseInt(result), result_message);
                    }

/*                    if(response.getStatusCode() != HttpStatus.OK){
                        throw new AppException(response.getStatusCode().value(), "Bad Request");
                    }*/

                    sqlSessionTemplate.update("slot.updateHistory", history);
                }
            }

        }

        if(count == 0){
            throw new AppException(-3838, "nothing to reward");
        }

    }

    private int getUserSpinCount(String userNo, String userKey, String gameNo){

        int spinCount = 0;
        HttpHeaders headers = new HttpHeaders();
        headers.add("userNo", userNo);
        headers.add("userKey", userKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try{
            response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/gamedata", HttpMethod.GET, new HttpEntity(headers), String.class);

            Map<String,Integer> map = new HashMap<String,Integer>();
            ObjectMapper mapper = new ObjectMapper();

            if(response.getBody() != null){
                map = mapper.readValue(response.getBody(), new TypeReference<HashMap<String,Integer>>(){});
                spinCount = map.get("spinCount");

            }
            logger.info(map.get("spinCount"));
        }catch (Exception ex){
            ex.printStackTrace();
            throw new AppException(-9000, "Bad Request");
        }
        return spinCount;
    }



    @GetMapping("/game/{gameNo}/test")
    @ResponseBody
    public void test(@RequestHeader(value="userNo") String userNo,
                           @RequestHeader(value="userKey") String userKey,
                           @PathVariable String gameNo) throws IOException {

        String xxx = "{\"message\":{\"result\":404,\"result_msg\":\"user not found\"},\"status\":200}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(xxx);

        JsonNode message = actualObj.get("message");

        String result = message.get("result").toString();
        String result_message = message.get("result_msg").toString();

        logger.info(result);
        logger.info(result_message);

    }

    private void getCash(String userNo, String userKey, String gameNo, int cash, String ipAddr){
        GameItem gameItem = new GameItem();
        gameItem.setCash(cash);
        gameItem.setIpAddr(ipAddr);

        HttpHeaders headers = new HttpHeaders();
        headers.add("userNo", userNo);
        headers.add("userKey", userKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> httpEntity = new HttpEntity<Object>(gameItem, headers);

        ResponseEntity<String> response = null;
        try{
            response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/getCash", HttpMethod.POST, httpEntity, String.class);

        }catch (Exception ex){
            ex.printStackTrace();
            throw new AppException(-9977, "Bad Request");
        }

        if(response.getStatusCode() != HttpStatus.OK){
            throw new AppException(-9976, "Bad Request");
        }
    }

}
