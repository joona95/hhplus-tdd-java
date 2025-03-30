package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class PointHistoryTableIntegrationTest {

    private static final long ANY_AMOUNT = 1000L;
    private static final TransactionType ANY_TRANSACTION_TYPE = TransactionType.CHARGE;
    private static final long ANY_UPDATE_MILLIS = 1L;

    PointHistoryTable pointHistoryTable;

    @BeforeEach
    void before() {
        pointHistoryTable = new PointHistoryTable();
    }

    @Nested
    class 포인트_내역_저장 {

        @Test
        void 포인트_내역_정보를_전달했을_때_포인트_내역을_저장() {

            //when
            pointHistoryTable.insert(1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //then
            assertThat(pointHistoryTable.selectAllByUserId(1L))
                    .hasSize(1)
                    .isEqualTo(List.of(
                            new PointHistory(1L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS)
                    ));
        }

        @Test
        void 포인트_내역_정보를_전달했을_때_해당_정보를_가진_포인트_내역을_반환() {

            //when
            PointHistory result = pointHistoryTable.insert(1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //then
            assertThat(result).isEqualTo(new PointHistory(1L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
        }
    }

    @Nested
    class 포인트_내역_전체_조회 {

        @Test
        void 특정_유저_아이디로_포인트_내역_목록_조회_시_해당_유저의_모든_포인트_내역_반환() {

            //given
            pointHistoryTable.insert(1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);
            pointHistoryTable.insert(1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);
            pointHistoryTable.insert(1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //when
            List<PointHistory> result = pointHistoryTable.selectAllByUserId(1L);

            //then
            assertThat(result)
                    .hasSize(3)
                    .isEqualTo(List.of(
                            new PointHistory(1L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                            new PointHistory(2L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                            new PointHistory(3L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS)
                    ));
        }

        @Test
        void 특정_유저_아이디로_포인트_내역_목록_조회_시_해당_유저에_해당하는_값이_없을_때_빈_리스트_반환() {

            //when
            List<PointHistory> result = pointHistoryTable.selectAllByUserId(1L);

            //then
            assertThat(result)
                    .hasSize(0)
                    .isEqualTo(List.of());
        }
    }
}