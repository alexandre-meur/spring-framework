package com.training.spring.bigcorp.service.measure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.model.SimulatedCaptor;
import com.training.spring.bigcorp.model.Site;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@RestClientTest(SimulatedMeasureService.class)
@ContextConfiguration(classes = {MeasureServiceConfigurationTest.class})
@EnableConfigurationProperties
public class SimulatedMeasureServiceTest {

    @Autowired
    private SimulatedMeasureService service;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper objectMapper;


    private SimulatedCaptor captor = new SimulatedCaptor("test", new Site("SiteTest"));

    @Test
    public void readMeasuresThrowsExceptionWhenArgIsNull() {
        Instant start = Instant.parse("2018-09-01T22:00:00Z");
        Instant end = start.plusSeconds(60 * 60 * 24);

        assertThatThrownBy(() -> service.readMeasures(null, start, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("captor is required");
        assertThatThrownBy(() -> service.readMeasures(captor, null, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start is required");
        assertThatThrownBy(() -> service.readMeasures(captor, start, null,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("end is required");
        assertThatThrownBy(() -> service.readMeasures(captor, start, end, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("step is required");
        assertThatThrownBy(() -> service.readMeasures(captor, end, start,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start must be before end");
    }

    @Test
    public void readMeasures() throws Exception {
        Instant start = Instant.parse("2018-09-01T22:00:00Z");
        Instant end = Instant.parse("2018-09-01T22:30:00Z");
        captor.setMinPowerInWatt(500_000);
        captor.setMaxPowerInWatt(1_000_000);

        List<MeasureDto> expectedMeasures = Arrays.asList(
                new MeasureDto(Instant.parse("2018-09-01T22:00:00Z"), 1234),
                new MeasureDto(Instant.parse("2018-09-01T22:30:00Z"), 4567));
        String expectedJson = objectMapper.writeValueAsString(expectedMeasures);
        String request = "http://localhost:8090/measures?start=2018-09-01T22:00:00Z&" +
                "end=2018-09-01T22:30:00Z&min=500000&max=1000000&step=3600";

        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson, MediaType.APPLICATION_JSON));
        List<Measure> measures = service.readMeasures(captor, start, end, MeasureStep.ONE_HOUR);
        assertThat(measures).hasSize(2);

        // And we have a value for each hour of the period
        assertThat(measures)
                .extracting("instant", "valueInWatt")
                .containsExactly(
                        tuple(Instant.parse("2018-09-01T22:00:00Z"), 1234),
                        tuple(Instant.parse("2018-09-01T22:30:00Z"), 4567)
                );
    }
}