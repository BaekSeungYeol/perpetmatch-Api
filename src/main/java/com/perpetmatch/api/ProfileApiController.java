package com.perpetmatch.api;

import com.perpetmatch.Member.MemberService;
import com.perpetmatch.pet.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileApiController {

    private final PetService petService;
    private final MemberService memberService;


//    @PostMapping("/api/profile/pet")
//    public ResponseEntity createPet(@RequestBody PetRequest petRequest){
//        // TODO "name"들 바꿔야됨
//        Pet pet = petService.findOrCreateNew("name");
//        memberService.addPet("name", pet);
//        return ResponseEntity.ok().build();
//
//    }
}
