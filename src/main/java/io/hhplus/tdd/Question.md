# 💡질문
1. 디렉토리 구조 - 커스텀 exception 파일 위치는 어디에 두는지 
<hr>

2. 커스텀한 예외를 각각 만드는 방법 VS GlobalException 이라는 커스텀 예외클래스를 정의한 후 Enum으로 예외를 관리하는 방법
- 그냥 컨벤션을 따라가는건지, 성능의 차이가 있는건지
```java
@Getter
public class GlobalBusinessException extends RuntimeException{

	private final String message;
	private final HttpStatus httpStatus;

	public GlobalBusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.message = errorCode.getMessage();
		this.httpStatus = errorCode.getHttpStatus();
	}
}
```

```java
@ExceptionHandler
public ResponseEntity<ErrorResult> globalException(GlobalBusinessException e) {
    return new ResponseEntity<>(new ErrorResult(e.getMessage(),e.getHttpStatus()),e.getHttpStatus());
}
```

```java
public interface ErrorCode {
    String getMessage();
    HttpStatus getHttpStatus();
}
```

```java
@Getter
@RequiredArgsConstructor
public enum ArticleErrorCode implements ErrorCode {
	ARTICLE_NOT_FOUND("게시글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD("비밀번호가 일치하지 않습니다.",HttpStatus.BAD_REQUEST),
	COMMENT_NOT_FOUND("댓글이 존재하지 않습니다.",HttpStatus.BAD_REQUEST),
	INVALID_WRITER("작성한 사용자가 아닙니다.",HttpStatus.BAD_REQUEST);

	private final String message;
	private final HttpStatus httpStatus;
}
```

<hr/>

3. ControllerTest,ServiceTest,RepositoryTest 시 유효성검사, 경계값 검사등을 매번해야하는지? 3번과 같은 맥락
   [PointRepository.java](point%2FPointRepository.java)
<hr>

4. 개발자가 생각하지 못하는 케이스에 대해서는  발생할때마다 추가하는건지? > 커버리지와 관련?
<hr>

