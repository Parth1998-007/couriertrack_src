package com.couriertrack.network;


import com.couriertrack.api_model.AcceptedCourierModel;
import com.couriertrack.api_model.BankDetailModel;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.api_model.CancelOrderModel;
import com.couriertrack.api_model.CheckVerifyModel;
import com.couriertrack.api_model.CityListModel;
import com.couriertrack.api_model.CompleteDeliveredModel;
import com.couriertrack.api_model.CourierOrderList;
import com.couriertrack.api_model.CreateOrderModel;
import com.couriertrack.api_model.GetPricingModel;
import com.couriertrack.api_model.LoginModel;
import com.couriertrack.api_model.NewPickupOrderDetailModel;
import com.couriertrack.api_model.NewPickupOrderListModel;
import com.couriertrack.api_model.OrderDetailModel;
import com.couriertrack.api_model.OrderListModel;
import com.couriertrack.api_model.PasswordChangeModel;
import com.couriertrack.api_model.PickupCourierModel;
import com.couriertrack.api_model.PricingModel;
import com.couriertrack.api_model.ResendOTPModel;
import com.couriertrack.api_model.SendOtpModel;
import com.couriertrack.api_model.SignUpModel;
import com.couriertrack.api_model.TransactionModel;
import com.couriertrack.api_model.UpdateIDModel;
import com.couriertrack.api_model.UpdateLocationModel;
import com.couriertrack.api_model.UpdateProfileModel;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("Login")
    Observable<Response<LoginModel.LoginRes>> login(@Body LoginModel.LoginReq loginReq);

    @POST("Auth/DriverLogin")
    Call<LoginModel.LoginRes> login2(@Body LoginModel.LoginReq loginReq);

    @POST("SendOTP")
    Observable<Response<SendOtpModel.SendOtpRes>> sendOtpreq(@Body SendOtpModel.SendOtpreq sendOtpreq);

    @POST("Forgotpass")
    Observable<Response<BaseRes>> changePassword(@Body PasswordChangeModel.ChangePassword changePassReq);

    @Multipart
    @POST("Signup")
    Observable<Response<SignUpModel.SignUpRes>> signup(@Part("device_type") RequestBody devicetype,@Part("user_type") RequestBody user_type,@Part("email") RequestBody email,@Part("first_name") RequestBody firstname,@Part("last_name") RequestBody lastname,@Part("password") RequestBody password,@Part("mobile") RequestBody mobile,@Part("token") RequestBody token,@Part MultipartBody.Part frontimg,@Part MultipartBody.Part backimg,@Part("doc_type") RequestBody doctype,@Part("doc_number") RequestBody docnumber,@Part("gender") RequestBody gender ,@Part MultipartBody.Part profile);

    @Multipart
    @POST("UpdateID")
    Observable<Response<UpdateIDModel.UpdateProfileIDRes>> updateID(@Part("user_id") RequestBody userid, @Part("device_type") RequestBody devicetype,@Part("user_type") RequestBody user_type ,@Part("token") RequestBody token ,@Part MultipartBody.Part frontimg, @Part MultipartBody.Part backimg, @Part("doc_type") RequestBody doctype, @Part("doc_number") RequestBody docnumber);

    @Multipart
    @POST("UpdateProfile")
    Observable<Response<UpdateProfileModel.UpdateProfileRes>> updateProfile(@Part("user_id") RequestBody userid, @Part("user_type") RequestBody user_type ,@Part("email") RequestBody email,@Part("first_name") RequestBody firstname,@Part("last_name") RequestBody lastname, @Part("mobile") RequestBody mobile, @Part MultipartBody.Part profileimg, @Part("gender") RequestBody gender);

    @GET("idList")
    Observable<Response<SendOtpModel.SendOtpRes>> idList();

    @Headers({"Content-Type: application/json"})
    @POST("CreateOrder")
    Observable<Response<CreateOrderModel.CreateOrderRes>> createOrder(@Body CreateOrderModel.CreateOrderReq createOrderReq);

    @GET("orderList/user_id/{user_id}")
    Observable<Response<OrderListModel.OrderListRes>> orderlist(@Path("user_id") int userId);

    @GET("orderDetails/user_id/{user_id}/order_id/{order_id}")
    Observable<Response<OrderDetailModel.OrderDetailRes>> orderDetail(@Path("user_id") int userid,@Path("order_id") int orderid);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/newPickup")
    Observable<Response<NewPickupOrderListModel.PickUpOrderListRes>> newpickorderlist(@Body NewPickupOrderListModel.NewPickupListReq newPickupListReq);

    @GET("courierBoy/orderDetails/user_id/{user_id}/order_id/{order_id}")
    Observable<Response<NewPickupOrderDetailModel.NewPickupOrderDetailRes>> pickupOrderDetail(@Path("user_id") int userid, @Path("order_id") int orderid);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/acceptOrder")
    Observable<Response<AcceptedCourierModel.OrderAcceptRes>> acceptOrder(@Body AcceptedCourierModel.OrderAcceptReq acceptOrderReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/myOrders")
    Observable<Response<CourierOrderList.OrderListRes>> courierOrderList(@Body CourierOrderList.OrderListReq courierOrderListReq);

    @FormUrlEncoded
    @POST("checkUserVerified")
    Observable<Response<CheckVerifyModel.checkVerifiedUserRes>> checkVerifyUser(@Field("user")int user_type,@Field("user_id")int userId);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/pickupOrder")
    Observable<Response<PickupCourierModel.PickupOrderRes>> pickupOrder(@Body PickupCourierModel.PickupOrderReq pickupOrderReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/deliveredOrder")
    Observable<Response<CompleteDeliveredModel.completeDeliveredRes>> completeOrder(@Body CompleteDeliveredModel.completeDeliveredReq completeOrderReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/resendOtp")
    Observable<Response<ResendOTPModel.SendOTPRes>> resendOTP(@Body ResendOTPModel.SendOTPReq sendOTPReq);

    @Headers({"Content-Type: application/json"})
    @POST("getPricing")
    Observable<Response<GetPricingModel.GetPricingResponse>> getPricing(@Body GetPricingModel.GetPricingReq getPricingReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/updateLatlong")
    Observable<Response<UpdateLocationModel.LocationRes>> updateLocation(@Body UpdateLocationModel.LocationReq locationReq);


    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/updateBankDetails")
    Observable<Response<BankDetailModel.BankDetailRes>> bankDetail(@Body BankDetailModel.BankDetailReq bankDetailReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/getTransactionList")
    Observable<Response<TransactionModel.TransactionRes>> getTrasactionList(@Body TransactionModel.TransactionReq transactionReq);

    @Headers({"Content-Type: application/json"})
    @POST("courierBoy/getBankDetails")
    Observable<Response<BankDetailModel.BankDetailRes>> getBankDetail(@Body BankDetailModel.BankDetailReq bankDetailReq);


    @Headers({"Content-Type: application/json"})
    @POST("userTransactionList")
    Observable<Response<TransactionModel.TransactionRes>> transactionlist(@Body TransactionModel.TransactionReq transactionReq);

    @Headers({"Content-Type: application/json"})
    @POST("CancelOrder")
    Observable<Response<CancelOrderModel.CancelOrderRes>> cancelOrder(@Body CancelOrderModel.CancelOrderReq cancelOrderReq);

    @Headers({"Content-Type: application/json"})
    @GET("CityList/{user_id}")
    Observable<Response<CityListModel.CityListRes>> cityList(@Path("user_id")int userid);

    @Headers({"Content-Type: application/json"})
    @GET("viewPricing/user_id/{user_id}")
    Observable<Response<PricingModel.PricingRes>> pricing(@Path("user_id")int userid);

}
