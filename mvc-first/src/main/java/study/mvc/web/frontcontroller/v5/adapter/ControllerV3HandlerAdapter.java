package study.mvc.web.frontcontroller.v5.adapter;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import study.mvc.web.frontcontroller.ModelView;
import study.mvc.web.frontcontroller.v3.ControllerV3;
import study.mvc.web.frontcontroller.v5.MyHandlerAdapter;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
	@Override
	public boolean supports(Object handler) {
		return (handler instanceof ControllerV3);
	}

	@Override
	public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		ControllerV3 controller = (ControllerV3)handler;
		Map<String, String> paramMap = createParamMap(request);
		ModelView mv = controller.process(paramMap);
		return mv;
	}

	private Map<String, String> createParamMap(HttpServletRequest request) {
		Map<String, String> paramMap = new HashMap<>();

		request.getParameterNames().asIterator()
			.forEachRemaining(paramName -> paramMap.put(paramName,
				request.getParameter(paramName)));
		return paramMap;
	}
}