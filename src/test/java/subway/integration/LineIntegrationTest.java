package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static subway.integration.step.LineStep.노선_생성_요청;
import static subway.integration.step.LineStep.노선_조회_요청;
import static subway.integration.step.SectionStep.구간_생성_요청;
import static subway.integration.step.SectionStep.구간_생성_요청을_생성한다;
import static subway.integration.step.StationStep.역_생성_요청;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionCreateRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
public class LineIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선을_생성한다() {
        // given
        final LineRequest 요청 = new LineRequest("2호선");

        // when
        ExtractableResponse<Response> response = 노선_생성_요청(요청);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header(HttpHeaders.LOCATION)).isEqualTo("/lines/1")
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 기존에_존재하는_지하철_노선_이름으로_노선을_생성하면_예외가_발생한다() {
        // given
        final LineRequest 요청 = new LineRequest("2호선");
        노선_생성_요청(요청);

        // when
        ExtractableResponse<Response> response = 노선_생성_요청(요청);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message"))
                        .isEqualTo(ErrorCode.DUPLICATE_NAME.getErrorMessage())
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final LineRequest 노선_요청 = new LineRequest("2호선");
        ExtractableResponse<Response> 응답 = 노선_생성_요청(노선_요청);
        역_생성_요청("잠실역", 1L);
        역_생성_요청("잠실새내역", 1L);
        구간_생성_요청(구간_생성_요청을_생성한다(1L, 2L, 30), 1L);

        // when
        Long 노선_ID = Long.parseLong(응답.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{노선_ID}", 노선_ID)
                .then().log().all()
                .extract();

        // then
        LineResponse lineResponse = response.as(LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getId()).isEqualTo(노선_ID),
                () -> assertThat(lineResponse.getName()).isEqualTo(노선_요청.getName()),
                () -> assertThat(lineResponse.getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("잠실역", "잠실새내역")
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final LineRequest 이호선_생성_요청 = new LineRequest("2호선");
        ExtractableResponse<Response> 이호선_생성_응답 = 노선_생성_요청(이호선_생성_요청);
        역_생성_요청("잠실역", 1L);
        역_생성_요청("잠실새내역", 1L);
        구간_생성_요청(구간_생성_요청을_생성한다(1L, 2L, 30), 1L);

        final LineRequest 삼호선_생성_요청 = new LineRequest("3호선");
        ExtractableResponse<Response> 삼호선_생성_응답 = 노선_생성_요청(삼호선_생성_요청);

        // when
        ExtractableResponse<Response> response = 노선_조회_요청();

        // then
        List<Long> expectedLineIds = Stream.of(이호선_생성_응답, 삼호선_생성_응답)
                .map(it -> Long.parseLong(it.header(HttpHeaders.LOCATION).split("/")[2]))
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("")).hasSize(2),
                () -> assertThat(response.jsonPath().getLong("[0].id")).isEqualTo(expectedLineIds.get(0)),
                () -> assertThat(response.jsonPath().getString("[0].name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getList("[0].stations", StationResponse.class))
                        .extracting(StationResponse::getName)
                        .containsExactly("잠실역", "잠실새내역"),
                () -> assertThat(response.jsonPath().getLong("[1].id")).isEqualTo(expectedLineIds.get(1)),
                () -> assertThat(response.jsonPath().getString("[1].name")).isEqualTo("3호선"),
                () -> assertThat(response.jsonPath().getList("[1].stations", StationResponse.class)).isEmpty()
        );
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        LineRequest 노선_생성_요청 = new LineRequest("2호선");

        노선_생성_요청(노선_생성_요청);

        역_생성_요청("잠실역", 1L);
        역_생성_요청("잠실새내역", 1L);

        // when
        SectionCreateRequest 요청 = 구간_생성_요청을_생성한다(1L, 2L, 10);
        ExtractableResponse<Response> response = 구간_생성_요청(요청, 1L);

        // then
        assertAll(
                () -> AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> AssertionsForClassTypes.assertThat(response.header(HttpHeaders.LOCATION))
                        .isEqualTo("/lines/1/sections/1")
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선에 구간을 추가한다.")
    void createSectionInExistLine() {
        // given
        LineRequest 노선_생성_요청 = new LineRequest("2호선");

        노선_생성_요청(노선_생성_요청);

        역_생성_요청("잠실역", 1L);
        역_생성_요청("잠실새내역", 1L);
        역_생성_요청("종합운동장역", 1L);
        구간_생성_요청(구간_생성_요청을_생성한다(1L, 2L, 10), 1L);

        // when
        SectionCreateRequest 새로운_구간_생성_요청 = 구간_생성_요청을_생성한다(2L, 3L, 7);
        ExtractableResponse<Response> response = 구간_생성_요청(새로운_구간_생성_요청, 1L);

        // then
        assertAll(
                () -> AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> AssertionsForClassTypes.assertThat(response.header(HttpHeaders.LOCATION))
                        .isEqualTo("/lines/1/sections/2")
        );
    }

    @DisplayName("역이 두 개 존재하는 노선의 지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        LineRequest 노선_생성_요청 = new LineRequest("2호선");
        노선_생성_요청(노선_생성_요청);

        역_생성_요청("잠실역", 1L);
        역_생성_요청("잠실새내역", 1L);
        구간_생성_요청(구간_생성_요청을_생성한다(1L, 2L, 10), 1L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/lines/1/stations/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
