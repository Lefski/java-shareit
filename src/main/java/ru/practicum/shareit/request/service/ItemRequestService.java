package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemRequestService {


    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        log.info("Создается запрос на предмет {}", itemRequestDto.toString());
        itemRequestValidation(itemRequestDto, userId);
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с переданным id не существует", HttpStatus.NOT_FOUND));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        ItemRequestDto savedItemRequest = ItemRequestMapper.toItemRequestDto(repository.save(itemRequest));
        log.debug("Создан запрос на предмет {}", savedItemRequest);
        return savedItemRequest;
    }

    public List<ItemRequestDto> getAllItemRequestsByOwner(Integer userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с переданным id не существует", HttpStatus.NOT_FOUND));
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = repository.findAllByRequestorId(userId, sort);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest :
                itemRequests) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            itemRequestDto = findRequestItems(itemRequestDto);
            itemRequestDtos.add(itemRequestDto);
        }
        return itemRequestDtos;
    }

    private ItemRequestDto findRequestItems(ItemRequestDto itemRequestDto) {
        List<Item> itemList = itemRepository.findByRequestId(itemRequestDto.getId());
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item :
                itemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setRequestId(itemRequestDto.getId());
            itemDtoList.add(itemDto);
            //получается неэффективно с этими переборами для перевода в дто, но я не придумал более оптимальный способ
        }
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }

    public List<ItemRequestDto> getAllItemRequests(Integer userId, Integer offset, Integer pageSize) {


        Page<ItemRequest> itemRequestPage = repository.findAll(PageRequest.of(offset, pageSize, Sort.Direction.DESC, "created"));

        Page<ItemRequestDto> itemRequestDtos = itemRequestPage.map(ItemRequestMapper::toItemRequestDto);
        //я решил попробовать пагинацию делать через page, но в тестах нужен именно список
        List<ItemRequestDto> itemRequestDtoList = itemRequestDtos.getContent();
        List<ItemRequestDto> itemRequestDtosWithItemsList = new ArrayList<>();
        for (ItemRequestDto itemRequestDto :
                itemRequestDtoList) {
            //TODO: add if for request owner
            if (itemRequestDto.getRequestor().getId() != userId){
                itemRequestDto = findRequestItems(itemRequestDto);
                itemRequestDtosWithItemsList.add(itemRequestDto);
            }

        }
        return itemRequestDtosWithItemsList;
    }

    public ItemRequestDto getRequestById(Integer requestId, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с переданным id не существует", HttpStatus.NOT_FOUND));
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с переданным id не существует", HttpStatus.NOT_FOUND));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto = findRequestItems(itemRequestDto);
        return itemRequestDto;
    }

    public void itemRequestValidation(ItemRequestDto itemRequestDto, Integer userId) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty() || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Некорректное описание запроса", HttpStatus.BAD_REQUEST);
        }
    }

}
