package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    @DisplayName("포인트를 생성합니다.")
    void createPointTest() {

        // when
        Point point = Point.of();

        // then
        assertThat(point.getPoint()).isEqualTo(0);
    }

    @Test
    @DisplayName("포인트에 음수가 할당되면 예외를 던집니다.")
    void givenMinusValue_whenPointIsCharged_thenExceptionIsThrown() {

        //when & then
        assertThatThrownBy(() -> Point.of(-1L))
                .isInstanceOf(NegativeChargeAmountException.class);
    }

    @DisplayName("최대 포인트 초과 시 예외를 던집니다.")
    @ParameterizedTest
    @ValueSource(longs = {PointConstants.MAX_POINT+1,PointConstants.MAX_POINT+1000})
    void MaxAmount_Charge_ThrowException(Long point) {

        assertThatThrownBy(() -> Point.of(point))
                .isInstanceOf(MaxPointException.class);;
    }

    @DisplayName("최대 포인트 이하면 예외를 던지지 않습니다.")
    @ParameterizedTest
    @ValueSource(longs = {PointConstants.MAX_POINT,PointConstants.MAX_POINT-1})
    void ValidAmount_CreatePoint_Success(Long point) {

        // when & then
        assertThatCode(() -> Point.of(point))
                .doesNotThrowAnyException();
    }
}