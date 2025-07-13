package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PointService pointService;

    @Nested
    class 유저_포인트_조회 {

        @Test
        void 특정_유저의_유저_포인트가_존재하지_않을_때_유저_포인트_조회_시_포인트_잔고_0을_가진_빈_유저_포인트_반환() throws Exception {

            //when, then
            mockMvc.perform(get("/point/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(0L))
                    .andDo(print());
        }

        @Test
        void 특정_유저의_유저_포인트가_존재할_때_유저_포인트_조회_시_해당_유저의_유저_포인트_반환() throws Exception {

            //given
            UserPoint userPoint = pointService.charge(1L, 1000L);

            //when, then
            mockMvc.perform(get("/point/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(1000L))
                    .andExpect(jsonPath("$.updateMillis").value(userPoint.updateMillis()))
                    .andDo(print());
        }
    }
    @Nested
    class 포인트_내역_조회 {

        @Test
        void 특정_유저의_포인트_내역이_존재하지_않을_때_포인트_내역_조회_시_빈_목록_반환() throws Exception {

            //when, then
            mockMvc.perform(get("/point/{id}/histories", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0))
                    .andDo(print());
        }

        @Test
        void 특정_유저의_포인트_내역이_존재할_때_포인트_내역_조회_시_해당_유저의_포인트_내역_목록_반환() throws Exception {

            //given
            UserPoint userPoint = pointService.charge(1L, 1000L);

            //when, then
            mockMvc.perform(get("/point/{id}/histories", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1L))

                    .andExpect(jsonPath("$[0].userId").value(1L))

                    .andExpect(jsonPath("$[0].amount").value(1000L))

                    .andExpect(jsonPath("$[0].type").value(TransactionType.CHARGE.name()))

                    .andExpect(jsonPath("$[0].updateMillis").value(userPoint.updateMillis()))
                    .andDo(print());
        }
    }

    @Nested
    class 포인트_충전 {

        @Test
        void 특정_유저의_유저_포인트가_존재할_때_포인트_충전_시_해당_유저의_잔고에_충전_금액을_더한_유저_포인트_반환() throws Exception {

            //given
            pointService.charge(1L, 1000L);

            long request = 1000L;

            //when, then
            mockMvc.perform(patch("/point/{id}/charge", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(2000L))
                    .andDo(print());
        }

        @Test
        void 유저_아이디가_0_미만인_경우_포인트_충전_시_400_오류() throws Exception {

            //given
            long request = 1000L;

            //when, then
            mockMvc.perform(patch("/point/{id}/charge", -1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("유저 아이디 값은 0 이상이어야 합니다."))
                    .andDo(print());
        }

        @Test
        void 충전_금액이_0_이하인_경우_포인트_충전_시_400_오류() throws Exception {

            //given
            long request = 0L;

            //when, then
            mockMvc.perform(patch("/point/{id}/charge", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("충전 금액은 0보다 커야 합니다."))
                    .andDo(print());
        }

        @Test
        void 충전_금액과_유저_포인트의_합이_최대_한도를_넘는_경우_포인트_충전_시_400_오류() throws Exception {

            //given
            pointService.charge(1L, 100000L);

            long request = 100001L;

            //when, then
            mockMvc.perform(patch("/point/{id}/charge", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("최대 한도를 초과하여 충전하는 것은 불가능합니다."))
                    .andDo(print());
        }
    }

    @Nested
    class 포인트_사용 {


        @Test
        void 특정_유저의_유저_포인트가_존재할_때_포인트_사용_시_해당_유저의_잔고에_사용_금액을_뺀_유저_포인트_반환() throws Exception {

            //given
            pointService.charge(1L, 2000L);

            long request = 1000L;

            //when, then
            mockMvc.perform(patch("/point/{id}/use", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.point").value(1000L))
                    .andDo(print());
        }

        @Test
        void 유저_아이디가_0_미만인_경우_포인트_사용_시_400_오류() throws Exception {

            //given
            long request = 1000L;

            //when, then
            mockMvc.perform(patch("/point/{id}/use", -1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("유저 아이디 값은 0 이상이어야 합니다."))
                    .andDo(print());
        }

        @Test
        void 사용_금액이_0_이하인_경우_포인트_사용_시_400_오류() throws Exception {

            //given
            long request = 0L;

            //when, then
            mockMvc.perform(patch("/point/{id}/use", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("사용 금액은 0보다 커야 합니다."))
                    .andDo(print());
        }

        @Test
        void 유저_포인트에서_사용_금액을_뺀_값이_0_미만인_경우_포인트_사용_시_400_오류() throws Exception {

            //given
            pointService.charge(1L, 1000L);

            long request = 1001L;

            //when, then
            mockMvc.perform(patch("/point/{id}/use", 1L)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("사용 가능한 금액을 초과하였습니다."))
                    .andDo(print());
        }
    }
}