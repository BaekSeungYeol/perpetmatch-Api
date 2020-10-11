package com.perpetmatch.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Domain.Order;
import com.perpetmatch.Domain.OrderItem;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.OrderItem.OrderItemRepository;
import com.perpetmatch.api.dto.Order.BagDetailsDto;
import com.perpetmatch.api.dto.Order.BagDto;
import com.perpetmatch.api.dto.Order.GetBagDto;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.util.Set;

import static javax.persistence.FetchType.LAZY;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class OrderApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    private Long id;
    private String token = null;

    @BeforeEach
    void beforeEach() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("12345678").build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("12345678")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();

        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }


    @Test
    @DisplayName("장바구니 추가 성공")
    void addBag() throws Exception {
        Long id = 283L;
        BagDto bagDto = new BagDto(3);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/order/bags/{id}", id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bagDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("장바구니에 아이템을 추가하였습니다."))
                .andDo(document("add-bag",
                        pathParameters(
                                parameterWithName("id").description("아이템 아이디")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("장바구니에 아이템을 추가하였습니다.")
                        )
                ));


        User user = userRepository.findByNickname("백승열입니다").get();
        User curUser = userRepository.findByIdWithBags(user.getId());
        assertThat(curUser.getBag().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("눌렀을 시 장바구니 아이템 삭제")
    void removeBagItem() throws Exception {

        Long bagId = 283L;

        User userId = userRepository.findByNickname("백승열입니다").get();
        User user = userRepository.findByIdWithBags(userId.getId());
        Item item = itemRepository.findById(bagId).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(3);
        orderItemRepository.save(orderItem);

        user.getBag().add(orderItem);


        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/order/bags/details/{id}", orderItem.getId())
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("장바구니에 아이템을 제거했습니다."))
                .andDo(document("remove-bag",
                        pathParameters(
                                parameterWithName("id").description("장바구니 아이템 아이디")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("장바구니에 아이템을 제거했습니다.")
                        )
                ));


        User curUser = userRepository.findByIdWithBags(userId.getId());
        assertThat(curUser.getBag().size()).isEqualTo(0);
    }


//    // TODO 장바구니 리스트 반환
//    @GetMapping("/order/bags")
//    public ResponseEntity getBags(@CurrentMember UserPrincipal currentMember) {
//        if (currentMember == null) {
//            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        Set<BagDetailsDto> bags = userService.getBags(currentMember.getId());
//        int totalSum = userService.getTotalSum(currentMember.getId());
//        GetBagDto data = new GetBagDto(totalSum, bags);
//        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "장바구니 리스트입니다.",data));
//    }

    @Test
    @DisplayName("장바구니 리스트 반환")
    void bagList() throws Exception {

        Long bagId = 283L;

        User userId = userRepository.findByNickname("백승열입니다").get();
        User user = userRepository.findByIdWithBags(userId.getId());
        Item item = itemRepository.findById(bagId).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(3);
        orderItemRepository.save(orderItem);

        user.getBag().add(orderItem);


        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/order/bags")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("bag-list",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("장바구니 리스트입니다."),
                                fieldWithPath("data.totalSum").type(JsonFieldType.NUMBER).description("장바구니 총합 계산"),
                                fieldWithPath("data.bags").type(JsonFieldType.ARRAY).description("장바구니"),
                                fieldWithPath("data.bags[0].id").type(JsonFieldType.NUMBER).description("사는 아이템 ID"),
                                fieldWithPath("data.bags[0].name").type(JsonFieldType.STRING).description("사는 아이템 이름"),
                                fieldWithPath("data.bags[0].price").type(JsonFieldType.NUMBER).description("사는 아이템 가격"),
                                fieldWithPath("data.bags[0].count").type(JsonFieldType.NUMBER).description("사는 아이템 갯수"))

                ));
    }

}