package com.sgu.findyourfriend.test;

import com.robotium.solo.Solo;
import com.sgu.findyourfriend.screen.MainLoginActivity;
import com.sgu.findyourfriend.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

public class LoginTest extends
		ActivityInstrumentationTestCase2<MainLoginActivity> {

	private static final String TAG = LoginTest.class.getSimpleName();
	private Solo solo;

	public LoginTest() {
		super(MainLoginActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	// notice: turn off auto login

	// testcase login fail
	public void testLoginFail() throws Exception {
		solo.assertCurrentActivity("wrong activity", MainLoginActivity.class);

		if (solo.waitForText("Lỗi kết nối mạng", 1, 500)) {
			solo.clickOnButton(0);

		} else {

			// solo.waitForView(R.id.EditText_Login_ID);

			EditText phoneLogin = (EditText) solo.getCurrentActivity()
					.findViewById(R.id.EditText_Login_ID);
			EditText passLogin = (EditText) solo.getCurrentActivity()
					.findViewById(R.id.EditText_Login_Password);

			// if (phoneLogin != null) Log.i(TAG, "view null");

			// clean edit text
			solo.clearEditText(phoneLogin);
			solo.clearEditText(passLogin);

			// endter info login
			solo.enterText(phoneLogin, "0979742111");
			solo.enterText(passLogin, "123456");

			solo.clickOnButton("Đăng Nhập");

			assertTrue("login fail", solo.searchText("không chính xác"));
		}

	}

	// testcase login success
	public void testLoginSuccess() throws Exception {
		solo.assertCurrentActivity("wrong activity", MainLoginActivity.class);

		if (solo.waitForText("Lỗi kết nối mạng", 1, 500)) {
			solo.clickOnButton(0);

		} else {

			// solo.waitForView(R.id.EditText_Login_ID);

			EditText phoneLogin = (EditText) solo.getCurrentActivity()
					.findViewById(R.id.EditText_Login_ID);
			EditText passLogin = (EditText) solo.getCurrentActivity()
					.findViewById(R.id.EditText_Login_Password);

			// if (phoneLogin != null) Log.i(TAG, "view null");

			// clean edit text
			solo.clearEditText(phoneLogin);
			solo.clearEditText(passLogin);

			// endter info login
			solo.enterText(phoneLogin, "0979742144");
			solo.enterText(passLogin, "111111");

			solo.clickOnButton("Đăng Nhập");

			assertTrue("screen loading fail", solo.searchText("đang tải..."));

			assertTrue("map fragment not found",
					solo.waitForView(R.id.mapFragment, 1, 10000));
		}

	}

	// testcase create account
	public void testCreateAccout() throws Exception {
		solo.assertCurrentActivity("wrong activity", MainLoginActivity.class);

		if (solo.waitForText("Lỗi kết nối mạng", 1, 500)) {
			solo.clickOnButton(0);

		} else {

			solo.clickOnButton("Đăng Kí");

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editName),
					"Test " + System.currentTimeMillis());

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editProvice),
					"provice " + System.currentTimeMillis());

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editEmail), "email");

			solo.clickOnButton("Kế tiếp");

			assertTrue("not catch empty field",
					solo.searchText("Nhập các thông tin yêu cầu"));

			solo.clickOnButton("Đóng");

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editEmail), "email@email.com");

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editName),
					"Test " + System.currentTimeMillis());

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editProvice),
					"provice " + System.currentTimeMillis());

			solo.clickOnText("Chọn tỉnh thành");

			solo.clickOnText("An Giang");

			solo.clickOnButton("Kế tiếp");

			solo.waitForView(R.id.btnNext, 1, 500);

			solo.clickOnButton("Kế tiếp");

			solo.waitForText("Thông tin tài khoản", 1, 1000);

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editPhoneNumber), "0979742144");

			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editPassword), "123456");

			solo.clickOnButton("Hoàn thành");

			assertTrue("not catch phone registed",
					solo.waitForText("Số điện thoại đã sử dụng", 1, 1500));

			solo.clickOnButton("Đóng");

			solo.clearEditText(0);

			// change phone here each test
			solo.enterText(
					(EditText) solo.getCurrentActivity().findViewById(
							R.id.editPhoneNumber), "01251024128");

			solo.clickOnButton("Hoàn thành");

			assertTrue("registion fail",
					solo.waitForText("Tạo tài khoản thành công", 1, 5000));

			solo.clickOnButton("Không");

			assertTrue("screen verify fail",
					solo.waitForText("Xác nhận email", 1, 1500));
		}
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();

	}

}
