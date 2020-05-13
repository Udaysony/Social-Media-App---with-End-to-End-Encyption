using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class UserController : ApiController
    {
        [HttpPost]
        //public String PostUser(string username, string password, string emailid, string firstname, string lastname, string mobile)
        public String PostUser(User user, string defaultGroupKey)
        {
            string query_status;
 
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                try
                {
                    context.Users.Add(user);

                    int? maxGroupID = (int?)context.Group_Status_Table.Max(g => (int?)g.groupid);
                    Group_Status_Table gst = new Group_Status_Table
                    {
                        groupid = (maxGroupID == null) ? 1 : (int)maxGroupID + 1,
                        groupname = user.username + "Friends",
                        status = 0
                    };
                    context.Group_Status_Table.Add(gst);

                    Group_Table gt = new Group_Table
                    {
                        username = user.username,   
                        groupid = gst.groupid,
                        groupname = gst.groupname,
                        isFriend = "yes",
                        isOwner = "yes"
                    };
                    context.Group_Table.Add(gt);

                    Group_Key_Table gk = new Group_Key_Table { 
                        
                        username = user.username,
                        groupKey = defaultGroupKey,
                        groupid = gst.groupid,
                        version_num = 1
                    };
                    context.Group_Key_Table.Add(gk);

                    int x = context.SaveChanges();
                    if ( x > 0)
                    {
                        query_status = "OK";
                        return query_status;
                    }
                    else
                    {
                        query_status = "failed";
                        return query_status;
                    }
                }
                catch (Exception e)
                {
                    query_status = e.ToString();
                    return query_status;
                }
            }
        }

        [HttpGet]
        public IQueryable UserAuth(string username, string password)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var user = context.Users.Where(u => u.username == username && u.password == password)
                    .Select(u => new
                    {
                        username = u.username,
                        password = u.password,
                    }).ToList();

                return user.AsQueryable();

            }

        }

        [HttpGet]
        public IQueryable FindFriends(string text, string username)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                

                var currently_following = context.Group_Table.Where(g => g.username == username &&
                                         g.isOwner == "no" && g.isFriend == "yes").ToList();


                if (text.Equals("All") || text.Equals("all") || text.Equals("ALL"))
                {

                    var users = context.Users.Where(u => u.username != username)
                           .Select(u => new
                           {
                               username = u.username,
                               groupname = u.username + "Friends"

                           }).ToList();

                    var result =
                                 from fnew in users
                                 from cur in currently_following
                                 where fnew.groupname != cur.groupname
                                 select fnew;

                    var result_ = result.ToList();

                    if (result_ == null || !(result_.Any())) return users.AsQueryable();
                    return result_.AsQueryable();

                }

                else
                {
                    var users = context.Users.Where(u => u.username.Contains(text) && u.username != username)
                        .Select(u => new
                        {
                            username = u.username,
                            groupname = u.username + "Friends"

                        }).ToList();


                    var result =
                                 from fnew in users
                                 from cur in currently_following
                                 where fnew.groupname != cur.groupname
                                 select fnew;

                    var result_ = result.ToList();
                    if (result_ == null || !(result_.Any())) return users.AsQueryable();


                    return result_.AsQueryable();
                }
            }
        }

        [HttpGet]
        public List<User> GetUserInfo(string username)
        {
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var user_info = context.Users.Where(u => u.username == username).ToList();
                return user_info;
            }
            
        }

        [HttpGet]
        public string GetPublicKey(string username)
        {
            string pk = "";
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var pub = context.Users.Single(u => u.username == username);
                pk = pub.publickey;
                return pk;
            }

        }

    }
}