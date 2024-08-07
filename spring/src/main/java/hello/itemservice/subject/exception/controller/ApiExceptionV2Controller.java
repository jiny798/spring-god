package hello.itemservice.subject.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hello.itemservice.subject.exception.UserException;
import hello.itemservice.subject.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

// @ExceptionHandler 적용

@Slf4j
@RestController
public class ApiExceptionV2Controller {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResult illegalExHandle(IllegalArgumentException e) {
		log.error("[exceptionHandle] ex", e);
		return new ErrorResult("BAD", e.getMessage());
	}

	//@ExceptionHandler
	public ResponseEntity<ErrorResult> userExHandle(UserException e) {
		log.error("[exceptionHandle] ex", e);
		ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
		return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	//@ExceptionHandler
	public ErrorResult exHandle(Exception e) {
		log.error("[exceptionHandle] ex", e);
		return new ErrorResult("EX", "내부 오류");
	}

	@GetMapping("/api2/members/{id}")
	public MemberDto getMember(@PathVariable("id") String id) {
		if (id.equals("ex")) {
			throw new RuntimeException("잘못된 사용자");
		}
		if (id.equals("bad")) {
			throw new IllegalArgumentException("잘못된 입력 값");
		}
		if (id.equals("user-ex")) {
			throw new UserException("사용자 오류");
		}
		return new MemberDto(id, "hello " + id);
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String memberId;
		private String name;
	}
}