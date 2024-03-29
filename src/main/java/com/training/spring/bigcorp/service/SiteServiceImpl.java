package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Lazy
@Transactional
public class SiteServiceImpl implements SiteService {

    private final static Logger logger = LoggerFactory.getLogger(SiteService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CaptorService captorService;

    public SiteServiceImpl() {
    }

    @Autowired
    public SiteServiceImpl(CaptorService captorService){
        logger.debug("Init SiteServiceImpl :" + this);
        this.captorService = captorService;
    }

    @Override
    public Site findById(String siteId) {
        logger.debug("Appel de findById avec l'id : "+siteId);

        if (siteId == null) {
            return null;
        }

        Site site = new Site("Florange");
        site.setId(siteId);
        site.setCaptors(captorService.findBySite(siteId));
        return site;
    }
}
