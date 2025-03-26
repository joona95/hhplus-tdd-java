package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserPointTableIntegrationTest {

    UserPointTable userPointTable;

    @BeforeEach
    void before() {
        userPointTable = new UserPointTable();
    }

    @Test
    void 특정_유저_아이디로_유저_포인트_조회_시_해당하는_유저의_유저_포인트_반환() {

        //given
        userPointTable.insertOrUpdate(1L, 1000L);

        //when
        UserPoint result = userPointTable.selectById(1L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(1000L);
    }

    @Test
    void 특정_유저_아이디로_유저_포인트_조회_시_해당하는_유저가_없는_경우_포인트_0을_가지는_빈_유저_포인트_반환() {

        //when
        UserPoint result = userPointTable.selectById(1L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(0L);
    }

    @Test
    void 유저_포인트_정보_전달하면_유저_포인트_저장_시_전달된_정보를_가지는_유저_포인트_반환() {

        //when
        UserPoint result = userPointTable.insertOrUpdate(1L, 1000L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(1000L);
    }

    @Test
    void 유저_포인트_정보_전달하면_유저_포인트_저장_시_해당_유저_정보를_가진_유저_포인트_정상적으로_저장() {

        //when
        UserPoint userPoint = userPointTable.insertOrUpdate(1L, 1000L);

        //then
        assertThat(userPointTable.selectById(1L)).isEqualTo(userPoint);
    }

    @Test
    void 유저_포인트_정보_전달하면_유저_포인트_저장_시_이미_유저_포인트_정보가_있는_경우_덮어씌우기() {

        //given
        userPointTable.insertOrUpdate(1L, 1000L);

        //when
        UserPoint userPoint = userPointTable.insertOrUpdate(1L, 2000L);

        //then
        assertThat(userPointTable.selectById(1L)).isEqualTo(userPoint);
    }
}