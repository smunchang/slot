package com.kaybo.slot.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class SlotController {

    private static Log logger = LogFactory.getLog(SlotController.class);

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @Value("${platform.url}")
    private String platformUrl;

    @GetMapping("/game/{gameNo}/playspin")
    @ResponseBody
    public Pool playspin(@RequestHeader(value="userNo") String userNo,
                         @RequestHeader(value="userKey") String userKey,
                         @PathVariable String gameNo) throws IOException {

        //sfEventSlot

        HttpHeaders headers = new HttpHeaders();
        headers.add("userNo", userNo);
        headers.add("userKey", userKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        //gameData 요청
        //  spin 횟수 갖고오기
        ResponseEntity<String> response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/user", HttpMethod.GET, new HttpEntity(headers), String.class);
        ObjectMapper mapper = new ObjectMapper();

        GameUser gameUser = mapper.readValue(response.getBody(), GameUser.class);

        logger.info(gameUser.getGameData());

        //Object object = ma
        GameData gameData = new GameData();
        if(gameUser.getGameData() == null){
            gameData.setSpinCount(0);
        }else{
            gameData = mapper.readValue( gameUser.getGameData(), GameData.class);

        }



        //kpcash 소진
        //  spin 횟수 업데이트

        int spinCount = gameData.getSpinCount() + 1;



        gameData.setSpinCount(spinCount);
        GameUser gu = new GameUser();
        gu.setCash(100);
        gu.setGameData(mapper.writeValueAsString(gameData));
        HttpEntity<?> httpEntity = new HttpEntity<Object>(gu, headers);

        ResponseEntity<String> response1 = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/cash", HttpMethod.POST, httpEntity, String.class);

        // spin 결과값 보여주기

        int count = sqlSessionTemplate.selectOne("slot.getSpinCount");

        Pool pool = sqlSessionTemplate.selectOne("slot.getShuffle", count);

        if(pool.getPaytableId() > 0){
            Paytable paytable = sqlSessionTemplate.selectOne("slot.getPaytable", pool.getPaytableId());
            pool.setMultiplier(paytable.getMultiplier());
            //todo
            //100 * multiplier
        }

        Reward reward = sqlSessionTemplate.selectOne("slot.selectReward", spinCount);
        if(reward != null){
            //todo
            //아이템 지급

            History history = new History();
            history.setUserNo(userNo);
            history.setRewardId(reward.getId());
            sqlSessionTemplate.insert("slot.insertHistory", history);

            pool.setReward(reward);

        }

        return pool;
    }


    @GetMapping("/game/{gameNo}/history")
    @ResponseBody
    public GameData userinfo(@RequestHeader(value="userNo") String userNo,
                         @RequestHeader(value="userKey") String userKey,
                         @PathVariable String gameNo) throws IOException {

        //sfEventSlot

        HttpHeaders headers = new HttpHeaders();
        headers.add("userNo", userNo);
        headers.add("userKey", userKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        //gameData 요청
        //  spin 횟수 갖고오기
        ResponseEntity<String> response = restTemplate.exchange(platformUrl + "/game/" + gameNo + "/user", HttpMethod.GET, new HttpEntity(headers), String.class);
        ObjectMapper mapper = new ObjectMapper();

        GameUser gameUser = mapper.readValue(response.getBody(), GameUser.class);

        logger.info(gameUser.getGameData());

        //Object object = ma
        GameData gameData = new GameData();
        if (gameUser.getGameData() == null) {
            gameData.setSpinCount(0);
        } else {
            gameData = mapper.readValue(gameUser.getGameData(), GameData.class);

        }

        List<History> items = sqlSessionTemplate.selectList("slot.listHistory", userNo);

        gameData.setItems(items);

        return gameData;
    }
}
