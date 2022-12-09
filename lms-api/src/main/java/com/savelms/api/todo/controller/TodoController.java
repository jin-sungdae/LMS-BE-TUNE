package com.savelms.api.todo.controller;

import com.savelms.api.commondata.APIDataResponse;
import com.savelms.api.todo.controller.dto.*;
import com.savelms.api.todo.service.TodoService;
import com.savelms.api.user.controller.error.ErrorResult;
import com.savelms.core.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TodoController {


    @ExceptionHandler
    public ResponseEntity<ErrorResult> entityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResult.builder()
                .message(e.getMessage())
                .build());
    }

    private final TodoService todoService;

    @PreAuthorize("hasAuthority('todo.create') OR "
        + "(hasAuthority('user.todo.create') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @Operation(description = "오늘 할 일 저장")
    @PostMapping("/users/{userId}/todos")
    public APIDataResponse<CreateTodoResponse> createTodo(@Validated @Parameter @RequestBody CreateTodoRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable("userId") String userId) {
        Long todoId = todoService.create(request, userId);
        return APIDataResponse.of(CreateTodoResponse.builder()
            .todoId(todoId)
            .build());
    }


    @PreAuthorize("hasAuthority('todo.read') OR "
        + "hasAuthority('user.todo.read')")
    @Operation(description = "오늘 내 할 일 가져오기")
    @GetMapping("/users/{userId}/todos")
    public APIDataResponse<ListResponse<GetMyTodosByDayResponse>> getMyTodosByToday(@PathVariable("userId") String userId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Parameter(name = "date", description = "date=2022-02-11", in = ParameterIn.QUERY)
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<GetMyTodosByDayResponse> todoDtos = todoService.getMyAllTodoInToday(
            userId,
            date == null ? LocalDate.now() : date);

        ListResponse<GetMyTodosByDayResponse> response = new ListResponse<>();
        response.setCount(todoDtos.size());
        response.setContent(todoDtos);
        return APIDataResponse.of(response);
    }

    @PreAuthorize("hasAuthority('todo.read')")
    @Operation(description = "오늘 할 일 모든 유저 가져오기")
    @GetMapping("/users/todos")
    public APIDataResponse<ListResponse<AllUserTodoDto>> getUserTodoAllByDay(
        @Parameter(name = "date", description = "date=2022-02-11", in = ParameterIn.QUERY)
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return APIDataResponse.of(todoService.getTodoAllByDay(date == null ? LocalDate.now() : date));
    }


    @PreAuthorize("hasAuthority('todo.update') OR "
        + "(hasAuthority('user.todo.update') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @PatchMapping("/users/{userId}/todos/{todoId}")
    public APIDataResponse<ResponseEntity<UpdateTodoResponse>> updateTodo(@Validated @Parameter @RequestBody UpdateTodoRequest request,
        @PathVariable("userId") String userId,
        @PathVariable("todoId") Long todoId){
        Long id = todoService.update(request, todoId, userId);

        UpdateTodoResponse responseBody = UpdateTodoResponse.builder()
            .todoId(id)
            .build();
        return APIDataResponse.of(new ResponseEntity<>(responseBody, HttpStatus.OK));
    }

    @PreAuthorize("hasAuthority('todo.delete') OR "
        + "(hasAuthority('user.todo.delete') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @DeleteMapping("/users/{userId}/todos/{todoId}")
    public APIDataResponse<ResponseEntity<DeleteTodoResponse>> deleteTodo(@PathVariable("userId") String userId,
        @PathVariable(name = "todoId") Long todoId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {

            Long id = todoService.delete(todoId, userId);
            DeleteTodoResponse responseBody = DeleteTodoResponse.builder()
                .todoId(id)
                .build();
        return APIDataResponse.of(new ResponseEntity<>(responseBody, HttpStatus.OK));
    }

}
