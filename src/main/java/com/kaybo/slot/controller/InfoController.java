package com.kaybo.slot.controller;

import com.kaybo.slot.model.Paytable;
import com.kaybo.slot.model.Pool;
import com.kaybo.slot.model.Slot;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class InfoController {

    private static Log logger = LogFactory.getLog(InfoController.class);

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @GetMapping("/info/makepools")
    @ResponseBody
    public String makepools() {

        Slot slot = sqlSessionTemplate.selectOne("slot.getSlot", 1);
        logger.info(slot.toString());


        List<Paytable> paytables = sqlSessionTemplate.selectList("slot.getPaytables");

        sqlSessionTemplate.delete("slot.deletePool");
        int count = 1;
        for(Paytable paytable : paytables){
            for(int i=0; i < paytable.getCountIn10000(); i++){
                Pool pool = new Pool();
                pool.setId(count);
                pool.setSymbols(paytable.getSymbolId() + "," + paytable.getSymbolId() + "," + paytable.getSymbolId());
                pool.setPayId(paytable.getId());

                sqlSessionTemplate.insert("slot.insertPool", pool);
                count++;
            }
        }

        while(count <= 10000){
            int s1 = (int)(Math.random()*6) + 1;
            int s2 = (int)(Math.random()*6) + 1;
            int s3 = (int)(Math.random()*6) + 1;

            if(s1 == s2 && s1 == s3){
                logger.info("countinue");
                continue;
            }else{
                logger.info(count + ": " + s1 + "-" + s2 + "-" + s3);
                Pool pool = new Pool();
                pool.setId(count);
                pool.setSymbols(s1 + "," + s2 + "," + s3);
                pool.setPayId(-1);
                sqlSessionTemplate.insert("slot.insertPool", pool);
                count++;
            }
        }
        return "OK";
    }


    @GetMapping("/info/makeshuffles")
    @ResponseBody
    public String test() {

        sqlSessionTemplate.delete("slot.deleteShuffle");
        sqlSessionTemplate.insert("slot.insertShuffle");

        return "OK " ;
    }


    @GetMapping("/info/playspin")
    @ResponseBody
    public Pool playspin() {

        int count = sqlSessionTemplate.selectOne("slot.getSpinCount");

        Pool pool = sqlSessionTemplate.selectOne("slot.getShuffle", count);

        return pool ;
    }


}
