package com.examonline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSupportTest {
    private final JsonSupport jsonSupport = new JsonSupport(new ObjectMapper());

    @Test
    void normalizeTargetClassesTreatsAllClassTokensAsEmptyScope() {
        assertThat(jsonSupport.normalizeTargetClasses(List.of("全部班级"))).isEmpty();
        assertThat(jsonSupport.normalizeTargetClasses(List.of(" all "))).isEmpty();
        assertThat(jsonSupport.readTargetClasses("[\"所有班级\"]")).isEmpty();
    }

    @Test
    void normalizeTargetClassesTrimsAndDeduplicatesClassNames() {
        assertThat(jsonSupport.normalizeTargetClasses(List.of(" 计算机 2301 班 ", "计算机 2301 班", "软件 2302 班")))
                .containsExactly("计算机 2301 班", "软件 2302 班");
    }
}
