package com.example.socialapp.webservice;

import com.example.socialapp.models.BothKeys;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.GroupInvitationDetails;
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.models.GroupStatusDetails;
import com.example.socialapp.models.PostDetails;
import com.example.socialapp.models.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("api/User/UserAuth")
    Call<List<UserDetails>> authenticate(@Query("username") String username, @Query("password") String password);

    @GET("api/User/GetProfile") //yet to implement
    Call<List<UserDetails>> getProfile(@Query("username") String username);

    @POST("api/User/PostUser") // it will Register Use and populate Group Tables......
    Call<String> addUser(@Body UserDetails user ,@Query("defaultGroupKey") String str_group_key) ;
//    Call<String> addAdmin(@Query("username") String username, @Query("password") String password, @Query("emailid") String emailid,@Query("firstname") String firstname, @Query("lastname") String lastname, @Query("mobile") String mobile);

    @GET("api/User/FindFriends")
    Call<List<UserDetails>> getFriends(@Query("text") String text);

    @GET("api/User/GetPublicKey")
    Call<String> getUserPublicKey(@Query("username") String username);



    @POST("api/Group_Invitation_Table/addRequest")
    Call<String> addRequest(@Body GroupInvitationDetails gi);

    @GET("api/Group_Invitation_Table/GetFriendsRequest")
    Call<List<GroupInvitationDetails>> getFriendRequests(@Query("username_to") String x);

    @GET("api/Group_Invitation_Table/GetGroupJoinRequests")
    Call<List<GroupInvitationDetails>> getGroupJoinRequests(@Query("username_to") String x);



    @POST("api/Group_Key_Table/AcceptRequest")
    Call<String> AcceptRequest(@Body GroupKeyDetails gk, @Query("owner") String x);

    @POST("api/Group_Key_Table/AcceptGroupRequest")
    Call<String> AcceptGroupRequest(@Body GroupKeyDetails gk,@Query("gname") String gname, @Query("owner") String owner);

    @GET("api/Group_Key_Table/GetGroupKey") // Get Key for Group
    Call<String> getGroupKey(@Query("gid") int gid, @Query("owner") String owner);



    @GET("api/Group_Table/GetCurrentFriends")
    Call<List<GroupDetails>> getCurrentFriends(@Query("uname") String x, @Query("gname") String y);

    @GET("api/Group_Table/getAllGroups")
    Call<List<GroupDetails>> getGroups(@Query("text") String text);

    @GET("api/Group_Table/GetCurrentGroups")
    Call<List<GroupDetails>> getCurrentGroups(@Query("uname") String text);

    @GET("api/Group_Table/GetGroupId")
    Call<GroupDetails> getGroupid(@Query("uname") String uname,@Query("gname") String gname);

    @POST("api/Group_Table/CreateNewGroup")
    Call<String> CreateGroup(@Query("owner") String owner, @Query("gname") String gname, @Query("group_key") String gk);

    @POST("api/Group_Table/PostAGroup")
    Call<String> postAgroup_inGroupTable(@Body GroupDetails gs);

    @GET("api/Group_Table/GetAllGroupsAndFriends")
    Call<List<GroupDetails>> getAll_Groups(@Query("uname") String text);



    @GET("api/Remove/RemoveGroup")
    Call<String> RemoveGroup(@Query("gid") int gid, @Query("gname") String gname, @Query("username") String username);


    @GET("api/Group_Status_Table/GetGroupInfo")
    Call<GroupStatusDetails> getGroupInfo(@Query("groupname") String groupname);

    @GET("api/Group_Status_Table/GetAllGroupInfo")
    Call<List<GroupStatusDetails>> getAllGroupInfo();

    @GET("api/Post_Table/GetAllPosts")
    Call<List<PostDetails>> getPosts();

    @POST("api/Post_Table/uploadPost")
    Call<String> PutPost(@Body PostDetails post);

    @GET("api/Group_Invitation_Table/RemoveRequest")
    Call<String> removeInvitation(@Query("owner") String string, @Query("new_user") String string1,@Query("groupname") String string2);
}
