package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.model.RealCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service("RealMeasureService")
@Lazy
public class RealMeasureService implements MeasureService<RealCaptor> {

    @Autowired
    private BigCorpApplicationProperties properties;

    @Override
    public List<Measure> readMeasures(RealCaptor captor, Instant start, Instant end, MeasureStep step) {
        checkReadMeasuresAgrs(captor, start, end, step);
        List<Measure> measures = new ArrayList<>();
        Instant current = start;
        while (current.isBefore(end)) {
            measures.add(new Measure(current,
                    properties.getMeasure().getDefaultReal(), captor));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measures;
    }

    public BigCorpApplicationProperties getProperties() {
        return properties;
    }

    public void setProperties(BigCorpApplicationProperties properties) {
        this.properties = properties;
    }
}
