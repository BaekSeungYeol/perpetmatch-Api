package com.perpetmatch.api;

import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.Order.OrderService;
import com.perpetmatch.api.dto.Order.BagDetailsDto;
import com.perpetmatch.api.dto.Order.BagDto;
import com.perpetmatch.api.dto.Order.AddressDto;
import com.perpetmatch.api.dto.Order.GetBagDto;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Transactional
public class OrderApiController {

    private final ItemRepository itemRepository;
    private final OrderService orderService;
    private final UserService userService;

    /**
     * feed인지, snack인지 goods인지
     * stockPrice, count
     */

//    @GetMapping("/order/bag")
//    public ResponseEntity showItem(@PathVariable Long id) {
//
//    }


    // TODO 장바구니 리스트 반환
    @GetMapping("/order/bags")
    public ResponseEntity getBags(@CurrentMember UserPrincipal currentMember) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Set<BagDetailsDto> bags = userService.getBags(currentMember.getId());
        int totalSum = userService.getTotalSum(currentMember.getId());
        GetBagDto data = new GetBagDto(totalSum, bags);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "장바구니 리스트입니다.",data));
    }


    /**
     * 장바구니 추가
     */
    @PostMapping("/order/bags/{id}")
    public ResponseEntity addBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                 @RequestBody BagDto bagDto){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.addBag(currentMember.getId(), id, bagDto.getCount());

        return ResponseEntity.ok().body(new ApiResponse(true, "장바구니에 아이템을 추가하였습니다."));
    }

    /**
     * 장바구니 삭제
     */
    @DeleteMapping("/order/bags/details/{id}")
    public ResponseEntity removeBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.removeBag(currentMember.getId(), id);

        return ResponseEntity.ok().body(new ApiResponse(true, "장바구니에 아이템을 제거했습니다."));
    }

    /**
     * 최종 결제시
     */
    @PostMapping("/order/{id}")
    public ResponseEntity addBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                 @RequestBody AddressDto addressDto){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        // TODO 주문
        orderService.createOrder(currentMember.getId(), addressDto);

        return ResponseEntity.ok().body(new ApiResponse(true, "주문 완료 하였습니다."));
    }


//    @PostMapping("/order/{id}")
//    public ResponseEntity createOrderItem(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
//                                   @RequestParam("count") int count){
//        if (currentMember == null) {
//            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        orderService.create(currentMember.getId(),id,count);
//    }


    //장바구니 추가, 장바구니 삭제


}
