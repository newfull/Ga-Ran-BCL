package vn.bcl.garanbcl.util;

import vn.bcl.garanbcl.users.SmartUser;

/**
 * Copyright (c) 2017 Codelight Studios
 * Created by kalyandechiraju on 22/04/17.
 */

public interface SmartLoginCallbacks {

    void onLoginSuccess(SmartUser user);

    void onLoginFailure(SmartLoginException e);

    SmartUser doCustomLogin();

    SmartUser doCustomSignup();
}
