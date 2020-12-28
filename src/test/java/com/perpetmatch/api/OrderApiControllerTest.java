package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Domain.OrderItem;
import com.perpetmatch.Domain.User;
import com.perpetmatch.jjwt.resource.ApiResponseCode;
import com.perpetmatch.jjwt.resource.AuthController;
import com.perpetmatch.modules.Item.ItemRepository;
import com.perpetmatch.modules.Member.UserRepository;
import com.perpetmatch.modules.Member.UserService;
import com.perpetmatch.modules.OrderItem.OrderItemRepository;
import com.perpetmatch.api.dto.Order.AddressDto;
import com.perpetmatch.api.dto.Order.BagDto;
import com.perpetmatch.common.RestDocsConfiguration;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    @Autowired
    AuthController authController;

    private Long id;
    private String token = null;

    @BeforeEach
    void beforeEach() throws Exception {
        signUp();
        getToken();
    }

    private void getToken() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("@!test1234")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();


        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }

    private void signUp() {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("@!test1234").build();

        authController.registerMember(request);
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
                        requestFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("물품 사는 갯수")
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
    @DisplayName("신규 아이템 조회")
    void getNew() throws  Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/items/new")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("아이템 신규 다건 조회 입니다."))
                .andDo(document("item-new",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("아이템 신규 다건 조회 입니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data.content[0].price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data.content[0].stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data.content[0].sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data.content[0].company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data.content[0].boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data.content[0].boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다.")
                        )));
    }
    @Test
    @DisplayName("베스트 아이템 조회")
    void getBest() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/items/best")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("아이템 베스트 다건 조회 입니다."))
                .andDo(document("item-best",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("아이템 베스트 다건 조회 입니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data.content[0].price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data.content[0].stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data.content[0].sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data.content[0].company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data.content[0].boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data.content[0].boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 단건 조회")
    void getOneItem() throws Exception {


        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/details/{id}", 283)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ApiResponseCode.OK.toString()))
                .andExpect(jsonPath("message").value("요청이 성공하였습니다."))
                .andDo(document("get-item",
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
                                fieldWithPath("code").type(JsonFieldType.STRING).description(ApiResponseCode.OK.toString()),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청이 성공하였습니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data.stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data.sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data.company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data.boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data.boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다."),
                                fieldWithPath("data.options").type(JsonFieldType.ARRAY).description("아이템 옵션 정보 입니다.")
                        )));
    }
    // 사료 간식 용품
    @Test
    @DisplayName("사료 다건 조회")
    void getListFeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/feeds")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("사료 리스트 다건 조회 입니다."))
                .andDo(document("list-feeds",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("간식 리스트 다건 조회 입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data[0].title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data[0].price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data[0].stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data[0].sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data[0].company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data[0].boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data[0].boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다.")
                        )));
    }

    @Test
    @DisplayName("간식 다건 조회")
    void gtListSnack() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/snacks")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("간식 리스트 다건 조회 입니다."))
                .andDo(document("list-snacks",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("용품 리스트 다건 조회 입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data[0].title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data[0].price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data[0].stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data[0].sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data[0].company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data[0].boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data[0].boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다.")
                        )));
    }
    @Test
    @DisplayName("용품 다건 조회")
    void getListGoods() throws Exception {


        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shop/goods")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("용품 리스트 다건 조회 입니다."))
                .andDo(document("list-goods",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("용품 리스트 다건 조회 입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("아이템 id 입니다."),
                                fieldWithPath("data[0].title").type(JsonFieldType.STRING).description("아이템 제목 입니다."),
                                fieldWithPath("data[0].price").type(JsonFieldType.NUMBER).description("아이템 가격 입니다."),
                                fieldWithPath("data[0].stockQuantity").type(JsonFieldType.NUMBER).description("아이템 수량 입니다."),
                                fieldWithPath("data[0].sale").type(JsonFieldType.NUMBER).description("아이템 세일 현황 입니다."),
                                fieldWithPath("data[0].company").type(JsonFieldType.STRING).description("아이템 제조회사 입니다."),
                                fieldWithPath("data[0].boardImageHead").type(JsonFieldType.STRING).description("아이템 이미지 입니다."),
                                fieldWithPath("data[0].boardImageMain").type(JsonFieldType.STRING).description("아이템 상세정보 입니다.")
                        )));
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
                                fieldWithPath("data.bags[0].id").type(JsonFieldType.NUMBER).description("사는 아이템 ID"),
                                fieldWithPath("data.bags[0].title").type(JsonFieldType.STRING).description("사는 아이템 이름"),
                                fieldWithPath("data.bags[0].price").type(JsonFieldType.NUMBER).description("사는 아이템 가격"),
                                fieldWithPath("data.bags[0].count").type(JsonFieldType.NUMBER).description("사는 아이템 갯수"),
                                fieldWithPath("data.bags[0].image").type(JsonFieldType.STRING).description("아이템 이미지"),
                                fieldWithPath("data.bags[0].company").type(JsonFieldType.STRING).description("아이템 제조회사"))

                ));
    }


    @Test
    @DisplayName("결제 완료 후 크레딧 차감")
    void payment_credit_deducted() throws Exception {

        Long bagId = 286L;

        User userId = userRepository.findByNickname("백승열입니다").get();
        User user = userRepository.findByIdWithBags(userId.getId());
        user.setCredit(100000);
        Item item = itemRepository.findById(bagId).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(3);
        orderItemRepository.save(orderItem);

        user.getBag().add(orderItem);

        AddressDto addressDto = new AddressDto();
        addressDto.setDear("홍길동");
        addressDto.setZipcode("12395");
        addressDto.setCity("서울 OO구 OO동");
        addressDto.setStreet("54-30");
        addressDto.setMemo("부재시 경비실에 맡겨주세요");


        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/order/bags/pay")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andDo(document("shop-payment",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        requestFields(
                                fieldWithPath("dear").type(JsonFieldType.STRING).description("받는 사람"),
                                fieldWithPath("zipcode").type(JsonFieldType.STRING).description("우편 번호"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("street").type(JsonFieldType.STRING).description("세부사항"),
                                fieldWithPath("memo").type(JsonFieldType.STRING).description("짧은 메모")
                                ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("주문 완료 하였습니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("주문자"),
                                fieldWithPath("data.totalSum").type(JsonFieldType.NUMBER).description("주문 총합"),
                                fieldWithPath("data.orderDate").type(JsonFieldType.STRING).description("주문 날짜"),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("유저의 남아있는 잔액"))

                                ));


        Optional<Item> byId = itemRepository.findById(286L);
        byId.ifPresent(i -> {
            Assertions.assertThat(i.getStockQuantity()).isEqualTo(97);
        });

        User curUser = userRepository.findByIdWithBags(userId.getId());
        Assertions.assertThat(curUser.getCredit()).isEqualTo(74500);
    }

}