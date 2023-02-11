package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 신분당선을 생성하고
     * When 강남역 - 양재역 구간 사이에 새로운 구간(강남역 - 청계산역)을 등록하면
     * Then 강남역 - 청계신역 - 양재역 구간이 생성된다.
     */
    @DisplayName("지하철 노선 추가")
    @Test
    void addStation() {
        Long 청계산역 = 지하철역_생성_요청("청계산역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 청계산역));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 청계산역, 양재역);
    }

    /**
     * Given 신분당선을 생성하고
     * When 강남역 - 양재역 구간 뒤에 새로운 구간(양재역 - 청계산역)을 등록하면
     * Then 강남역 - 양재역 - 청계산역 구간이 생성된다.
     */
    @DisplayName("지하철 노선 추가")
    @Test
    void addStation_2() {
        Long 청계산역 = 지하철역_생성_요청("청계산역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 청계산역));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 청계산역);
    }

    /**
     * Given 신분당선을 생성하고
     * When 강남역 - 양재역 구간 앞에 새로운 구간(서초역 - 강남역)을 등록하면
     * Then 서초역 - 강남역 - 양재역 구간이 생성된다.
     */
    @DisplayName("지하철 노선 추가")
    @Test
    void addStation_3() {
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서초역, 강남역));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(서초역, 강남역, 양재역);
    }

    /**
     * Given 신분당선을 생성하고
     * When 강남역 - 양재역 구간 사이에 새로운 구간(강남역 - 청계산역)을 등록할때 구간 간격이 기존의 구간보다 크면
     * Then 요청이 실패한다.
     */
    @DisplayName("지하철 노선 추가 실패")
    @Test
    void addStationFail() {
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 서초역, 20));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 신분당선을 생성하고
     * When 상행역과 하행역이 이미 등록되어 있으
     * Then 요청이 실패한다.
     */
    @DisplayName("지하철 노선 추가 실패")
    @Test
    void addStationFail_2() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역,20));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 신분당선을 생성하고
     * When 상행역과 하행역이 모두 등록되어 있지않으면
     * Then 요청이 실패한다.
     */
    @DisplayName("지하철 노선 추가 실패")
    @Test
    void addStationFail_3() {
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");
        Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서초역, 사당역, 20));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }




    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }
    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId,int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
