package ch.com.guessmusic.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import ch.com.guessmusic.utils.WechatUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	@Override protected void onCreate(Bundle savedInstanceState) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		//注册微信
		if (WechatUtil.iwxapi == null)
			WechatUtil.regToWx(this);
		api = WechatUtil.iwxapi;

		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override public void onReq(BaseReq baseReq) {
		int type = baseReq.getType();

	}

	@Override public void onResp(BaseResp baseResp) {
		int type = baseResp.errCode;
		switch (type){
			case BaseResp.ErrCode.ERR_OK:
				//Toast.makeText(WXEntryActivity.this, "分享成功！", Toast.LENGTH_SHORT);
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//Toast.makeText(WXEntryActivity.this, "分享被拒绝！", Toast.LENGTH_SHORT);
				break;
		}

		//返回MainActivity
		this.finish();
	}
}