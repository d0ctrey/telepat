package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import com.github.doctrey.telegram.client.util.NameUtils;
import com.github.doctrey.telegram.client.util.RpcUtils;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class RegistrationService {

    private static final String TAG = "RegistrationService";


    private TelegramApi api;
    private RpcUtils rpcUtils;

    public RegistrationService(TelegramApi api) {
        this.api = api;
        rpcUtils = new RpcUtils(api);
    }

    public void sendCode(String phoneNumber) {
        TLRequestAuthSendCode requestAuthSendCode = new TLRequestAuthSendCode();
        requestAuthSendCode.setApiId(ApiConstants.API_ID);
        requestAuthSendCode.setApiHash(ApiConstants.API_HASH);
        requestAuthSendCode.setPhoneNumber(phoneNumber);
        TLSentCode sentCode = rpcUtils.doRpc(requestAuthSendCode, false);

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET status = ? AND phone_code_hash = ? AND phone_registered = ? WHERE phone_number = ?")
            ) {

            statement.setInt(1, PhoneNumberStatus.CODE_SENT.getCode());
            statement.setString(2, sentCode.getPhoneCodeHash());
            statement.setBoolean(3, sentCode.isPhoneRegistered());
            statement.setString(4, phoneNumber);

            statement.executeUpdate();

        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public void verifyCode(String phoneNumber, String code) {
        /*BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.println("Enter Code: ");
        String code = null;
        try {
            code = reader.readLine();
        } catch (IOException e) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        boolean phoneRegistered = false;
        String codeHash = null;
        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tl_phone_numbers WHERE phone_number = ?")
        ) {
            statement.setString(1, phoneNumber);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    phoneRegistered = rs.getBoolean("phone_registered");
                    codeHash = rs.getString("phone_code_hash");
                }
            }

        } catch (SQLException e) {
            Logger.e(TAG, e);
        }

        TLAuthorization authorization;
        if (phoneRegistered) {
            TLRequestAuthSignIn requestAuthSignIn = new TLRequestAuthSignIn();
            requestAuthSignIn.setPhoneNumber(phoneNumber);
            requestAuthSignIn.setPhoneCodeHash(codeHash);
            requestAuthSignIn.setPhoneCode(code);
            authorization = rpcUtils.doRpc(requestAuthSignIn, false);
        } else {

            TLRequestAuthSignUp requestAuthSignUp = new TLRequestAuthSignUp();
            requestAuthSignUp.setFirstName(NameUtils.randomFirstName());
            requestAuthSignUp.setLastName(NameUtils.randomLastName());
            requestAuthSignUp.setPhoneCode(code);
            requestAuthSignUp.setPhoneNumber(phoneNumber);
            requestAuthSignUp.setPhoneCodeHash(codeHash);
            authorization = rpcUtils.doRpc(requestAuthSignUp, false);
        }

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET status = ? AND user_id = ? WHERE phone_number = ?")
        ) {
            statement.setInt(1, PhoneNumberStatus.REGISTERED.getCode());
            statement.setInt(2, authorization.getUser().getId());
            statement.setString(3, phoneNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }

        api.getState().doAuth(authorization);
        api.getState().setAuthenticated(api.getState().getPrimaryDc(), true);
    }

}
