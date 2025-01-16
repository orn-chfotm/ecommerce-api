package com.build.ecommerce.api.manager;

import com.build.ecommerce.core.dto.response.SuccessResponse;
import com.build.ecommerce.domain.user.dto.request.UserSearchRequest;
import com.build.ecommerce.domain.user.dto.response.UserResponse;
import com.build.ecommerce.domain.user.exception.UserNotFountException;
import com.build.ecommerce.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/user")
@RequiredArgsConstructor
@Tag(name = "회원", description = "관리자 페이지 회원 관련 Api")
@ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
        )
)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ManagerUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(method = "GET", summary = "Select User Infomation", description = "사용자 정보를 검색합니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = UserNotFountException.class))
                    )
            }
    )
    public ResponseEntity<SuccessResponse<UserResponse>> getUserDetail(@PathVariable Long userId) {
        return SuccessResponse.toResponse(userService.getUserDetail(userId));
    }

    @GetMapping
    @Operation(method = "GET", summary = "User List Infomation", description = "사용자 리스트를 검색합니다.")
    public ResponseEntity<SuccessResponse<List<UserResponse>>> getUserDetail(UserSearchRequest request) {
        return SuccessResponse.toResponse(userService.getUserList(request));
    }
}
