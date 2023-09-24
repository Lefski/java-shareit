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
        User requestor = UserMapper.toUser(userService.getUserById(userId));
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
            itemDtoList.add(ItemMapper.toItemDto(item));
            //получается неэффективно с этими переборами для перевода в дто, но я не придумал более оптимальный способ
        }
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }

    public Page<ItemRequestDto> getAllItemRequests(Integer userId, Integer offset, Integer pageSize) {


        Page<ItemRequest> itemRequestPage = repository.findAll(PageRequest.of(offset, pageSize, Sort.Direction.DESC));
        Page<ItemRequestDto> itemRequestDtos = itemRequestPage.map(new Function<ItemRequest, ItemRequestDto>() {
            @Override
            public ItemRequestDto apply(ItemRequest itemRequest) {
                //я не очень хорошо понимаю как сделать лямбда-функцию, поэтому выбрал такое решение
                return ItemRequestMapper.toItemRequestDto(itemRequest);
            }
        });


        return itemRequestDtos;
    }

    public ItemRequestDto getRequestById(Integer requestId) {
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
