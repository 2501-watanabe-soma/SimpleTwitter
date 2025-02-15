package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public EditServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	// top.jspから編集するMessageのidを取得
    	String editMessageId = request.getParameter("editMessageId");

    	HttpSession session = request.getSession();
    	List<String> errorMessages = new ArrayList<String>();

    	// idが数字以外の場合トップ画面に遷移し、エラーメッセージを表示
    	if (StringUtils.isBlank(editMessageId)) {
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

    	// idが数字以外の場合トップ画面に遷移し、エラーメッセージを表示
    	if (!editMessageId.matches("^[0-9]+$")) {
    		errorMessages.add("不正なパラメータが入力されました");
    		session.setAttribute("errorMessages", errorMessages);
    		response.sendRedirect("./");
    		return;
    	}

    	// MessageServiceのeditselectを呼び出す
    	Message editMessage = new MessageService().select(Integer.parseInt(editMessageId));

    	//idが存在しない場合トップ画面に遷移し、エラーメッセージを表示
    	if (editMessage == null) {
    		errorMessages.add("不正なパラメータが入力されました");
    		session.setAttribute("errorMessages", errorMessages);
    		response.sendRedirect("./");
    		return;
    	}

    	// 出力するデータ・jspを指定、フォワード
    	request.setAttribute("message", editMessage);
    	request.getRequestDispatcher("edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	String editText = request.getParameter("editText");
    	int editMessageId = Integer.parseInt(request.getParameter("editMessageId"));

    	List<String> errorMessages = new ArrayList<String>();

    	// beansに値をセット
    	Message message = new Message();
    	message.setText(editText);
    	message.setId(editMessageId);

    	if (!isValid(editText, errorMessages)) {
    		// エラーメッセージ表示
    		request.setAttribute("errorMessages", errorMessages);

    		request.setAttribute("message", message);
    		request.getRequestDispatcher("edit.jsp").forward(request, response);

    		return;
    	}

    	// MessageServiceを呼び出し後、トップページに遷移
    	new MessageService().update(message);
    	response.sendRedirect("./");

    }

    private boolean isValid(String text, List<String> errorMessages) {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	if (StringUtils.isBlank(text)) {
    		errorMessages.add("メッセージを入力してください");
    	} else if (140 < text.length()) {
    		errorMessages.add("140文字以下で入力してください");
    	}

    	if (errorMessages.size() != 0) {
    		return false;
    	}
    	return true;
    }
}
