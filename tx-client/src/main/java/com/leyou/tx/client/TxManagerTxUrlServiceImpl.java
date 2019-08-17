package com.leyou.tx.client;

import com.codingapi.tx.config.service.TxManagerTxUrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TxManagerTxUrlServiceImpl implements TxManagerTxUrlService {

    @Value("${lcc.manager.url}")
    private String txManagerTxUrl;

    public String getTxUrl() {
        return txManagerTxUrl;
    }

}
