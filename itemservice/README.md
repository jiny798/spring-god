## 검증 

### 1. BindingResult, FieldError

```JAVA
public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult)
```
! BindingResult 파라미터는 검증하는 ModelAttribute 뒤에 와야한다.


[예시]
```java
if (!StringUtils.hasText(item.getItemName())) {
    bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
}
```
이런식으로 직접 코드로 검증하고, BindingResult 에 에러를 추가하는 방법

```java
public FieldError(String objectName, String field, String defaultMessage) {} 
```
- ojbectName 은  @ModelAttribute가 적용된 변수 이름
- field는 오류가 발생한 필드의 변수이름
- defaultMessage 는 기본 오류 메시지를 설정한다.

<br>
특정 필드가 아닌, 특정 조건일 경우 ObjectError를 추가한다.

[예시]
```java
bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야합니다. 현재 값 = " + resultPrice));
```
```java
public ObjectError(String objectName, String defaultMessage) {}
```
- ojbectName 은  @ModelAttribute가 적용된 변수 이름
- defaultMessage 는 기본 오류 메시지를 설정한다.

타임리프

[예시]
```html
```html
<form action="item.html" th:action th:object="${item}" method="post">
    <div th:if="${#fields.hasGlobalErrors()}">
        <p class="field-error" th:each="err : ${#fields.globalErrors()}"
           th:text="${err}">글로벌 오류 메시지</p>
    </div>

    <div>
        <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
        <input type="text" id="itemName" th:field="*{itemName}"
               th:errorclass="field-error" class="form-control" placeholder="이름을 입력하세요">
        <div class="field-error" th:errors="*{itemName}">
            상품명 오류
        </div>
    </div>

```
- #fields : #fields.hasGlobalErrors() 로 BindingResult 가 제공하는 검증 오류에 접근할 수 있다.
- th:errors : 해당 필드에 오류가 있는 경우에 태그를 출력한다. (th:if와 유사)
- th:errorclass : th:field 에서 지정한 필드에 오류가 있으면 class 정보를 추가한다

<br>

### 2. BindingResult2

1단계에서 객체 필드를 직접 검수하고, 검증 오류가 생기면 BindingResult에 FieldError를 직접 추가했다.

그런데 그전에 @ModelAttribute에 바인딩 시 타입 오류가 발생한다면 어떻게 될까

- BindingResult 가 없다면 400 에러 페이지를 보내고,
- BindingResult 가 있다면 스프링이 직접 오류 정보( FieldError )를 BindingResult 에 담아서 컨트롤러를 정상 호출한다.

- 그리고 BindingResult 는 Model에 자동으로 포함된다.

### 3. 오류 코드와 메시지 처리 1

FieldError는 오류 메시지를 사용하기 위해 생성자를 하나 더 제공한다.

```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object
rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
Object[] arguments, @Nullable String defaultMessage)
```
- objectName : 오류가 발생한 객체 이름
- field : 오류 필드
- rejectedValue : 사용자가 입력한 값(거절된 값)
- bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- codes : 메시지 코드
- arguments : 메시지에서 사용하는 인자
- defaultMessage : 기본 오류 메시지

```properties
# application.properties
spring.messages.basename=messages,errors
```
```properties
# errors.properties
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

위와 같이 error 코드가 저장되어 있다면
```java
new FieldError("item", "price", item.getPrice(), false, new String[]
{"range.item.price"}, new Object[]{1000, 1000000}
```

5번째 인자로 range.item.price 코드가 전달되었다.
range.item.price 를 찾아 매칭되는 메시지를 사용한다. 그리고 new Object[]{1000, 1000000} 의 값이 {0},{1} 로 들어간다.

### 4. 오류 코드와 메시지 처리 2 (rejectValue() , reject())
FieldError , ObjectError 를 사용하지 않고, <br>
BindingResult 가 제공하는 rejectValue() , reject() 를 사용할 수 있다.

```java
void rejectValue(@Nullable String field, String errorCode, 
                    @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```
- field : 오류 필드
- errorCode : messageResolver 에서 사용하는 코드
- errorArgs : 오류 메세지 {숫자} 를 치환하기 위한 값
- defaultMessage : 오류 기본 메세지

```java
bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null)
```
FieldError() 를 사용할 때는 오류 코드를 "range.item.price" 이런식으로 명시해줘야 한다.

하지만 rejectValue 에 전체 코드가 없는데 해당 메세지를 찾을 수 있다.

스프링은 MessageCodesResolver 라는 것으로 이러한 기능을 지원한다.

<br>

### 5. MessageCodesResolver

```java
	MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

	@Test
	void messageCodesResolverObject() {
		String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        // errorCode -> required
        // objectName -> item

		assertThat(messageCodes).containsExactly("required.item", "required");
	}

	@Test
	void messageCodesResolverField() {
		String[] messageCodes = codesResolver.resolveMessageCodes("required",
			"item", "itemName", String.class);
        // errorCode -> required
        // objectName -> item
        // field itemName
		assertThat(messageCodes).containsExactly(
			"required.item.itemName",
			"required.itemName",
			"required.java.lang.String",
			"required"
		);
	}
```
- 에러코드,objectName만 있는 객체 오류는 다음 순서로 2가지 생성 <br>
1.: code + "." + object name <br>
2.: code <br>

- 필드 오류는 4가지 생성 <br>
  1.: code + "." + object name + "." + field <br>
  2.: code + "." + field <br>
  3.: code + "." + field type <br>
  4.: code <br>

<br>
BindingResult 의 rejectValue() , reject() 는 내부에서 MessageCodesResolver 를 사용

FieldError , ObjectError 의 생성자를 보면 오류 코드를 여러개 저장할 수 있으며,

MessageCodesResolver 를 통해서 생성된 순서대로 FieldError , ObjectError 에 저장


<br>

### MessageCodesResolver 를 이용한 오류 코드, 메시지 처리