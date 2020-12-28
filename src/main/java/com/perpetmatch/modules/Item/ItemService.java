package com.perpetmatch.modules.Item;

import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.api.dto.Order.ItemDtoOne;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.jjwt.resource.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDtoOne getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        return new ItemDtoOne(item);
    }
}
