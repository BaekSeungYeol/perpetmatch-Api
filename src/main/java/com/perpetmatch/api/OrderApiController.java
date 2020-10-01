package com.perpetmatch.api;

import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.Order.OrderRepository;
import com.perpetmatch.Order.OrderService;
import com.perpetmatch.api.dto.Board.BoardUpdateRequest;
import com.perpetmatch.api.dto.Order.DeliveryDto;
import com.perpetmatch.api.dto.Order.OrderDto;
import com.perpetmatch.api.dto.Profile.ProfileRequest;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    /**
     * 장바구니 추가
     */
    @PostMapping("/order/bag/{id}")
    public ResponseEntity addBag(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                          @RequestParam("count") int count){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.addBag(currentMember.getId(), id, count);

        return ResponseEntity.ok().body(new ApiResponse(true, "장바구니에 아이템을 추가하였습니다."));
    }

    /**
     * 장바구니 삭제
     */
    @DeleteMapping("/bag/{id}")
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
                                 @RequestBody DeliveryDto deliveryDto){
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        // TODO 주문

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
