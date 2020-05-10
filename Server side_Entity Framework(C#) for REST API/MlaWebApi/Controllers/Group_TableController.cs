using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class Group_TableController : ApiController
    {

        [HttpPost]
        //public String PostUser(string username, string password, string emailid, string firstname, string lastname, string mobile)
        public String PostGroup(Group_Table group_table)
        {
            string status;
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    context.Group_Table.Add(group_table);
                    if (context.SaveChanges() > 0)
                    {
                        status = "OK";
                        return status;
                    }
                    else
                    {
                        status = "f";
                        return status;
                    }
                }
                catch (Exception e)
                {
                    status = e.ToString();
                    return status;
                }
            }
        }

        [HttpPost]
        public string CreateNewGroup(string owner, string gname, string group_key)
        {
            string s = "";
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    int? maxGroupID = (int?)context.Group_Status_Table.Max(g => (int?)g.groupid);
                    Group_Status_Table gs = new Group_Status_Table
                    {
                        groupid = (maxGroupID == null) ? 1 : (int)maxGroupID + 1,
                        groupname = gname,
                        status = 0
                    };

                    context.Group_Status_Table.Add(gs);
                    s += "status_table_added ";

                    Group_Table gt = new Group_Table {

                        groupid = gs.groupid,
                        groupname = gname,
                        username = owner,
                        isOwner = "yes",
                        isFriend = "no"
                    };
                    context.Group_Table.Add(gt);
                    s += "group_table_added ";



                    Group_Key_Table gk = new Group_Key_Table
                        {

                            groupid = gs.groupid,
                            username = gt.username,
                            groupKey = group_key,
                            version_num = 1
                        };
                        context.Group_Key_Table.Add(gk);
                    s += "key_table_added";

                   

                    if (context.SaveChanges() > 0)
                    {
                        string status = "ok";
                        return status;

                        //return group_table.groupid;
                    }
                    else
                    {
                        string status = s + "failed";
                        return status;
                    }
                }
                catch (Exception e)
                {
                    string status = s + e.ToString();
                    return status;
                }
            }
        }

        [HttpGet]
        public List<Group_Table> getAllGroups(string text, string username)
        {
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var current = context.Group_Table.Where(g => g.username == username && g.isFriend == "no").ToList();

                if (text.Equals("All") || text.Equals("all") || text.Equals("ALL"))
                {

                    var gps = context.Group_Table.Where(g => g.username != username && g.isOwner == "yes"
                                                            && g.isFriend == "no" ).ToList();

                    return gps;

                }

                else
                {
                    var gps = context.Group_Table.Where(g => g.groupname.Contains(text) && g.username != username && g.isOwner=="yes"
                                    && g.isFriend == "no").ToList();

                    return gps;
                    var result = from new_g in gps
                                 join p in current
                                 on new_g.groupname equals p.groupname
                                 select new_g;

                    var result_ = result.ToList();

                    if (!(result_.Any())) return gps;

                    return result_;
                }


            }
        }

        [HttpGet]
        public List<Group_Table> getSearchNewFriends(string text, string owner)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Table.Where(g => g.groupname.Contains(text) && g.isOwner == "yes"
                && g.isFriend == "no").ToList();
                return gps;
            }
        }


        [HttpGet]
        public Group_Table getGroupId(string uname, string gname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Table.Single(g => g.groupname == gname && g.username == uname);
                return gps;
            }
        }

        [HttpGet]
        public List<Group_Table> GetCurrentFriends( string uname, string gname) { 
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var names = context.Group_Table.Where(g => g.groupname == gname && g.username != uname).ToList();
                return names;
            }
        }

        [HttpGet]
        public List<Group_Table> GetCurrentFriendsFollowing(string uname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Table.Where(g => g.username == uname &&
                                        g.isOwner == "no" && g.isFriend == "yes").ToList();
                return gps;
            }
        }

        [HttpGet]
        public List<Group_Table> GetCurrentGroups(string uname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var names = context.Group_Table.Where(g => g.username == uname && g.isFriend == "no").ToList();

                return names;
            }
        }

        [HttpGet]
        public List<Group_Table> GetAllGroupsAndFriends(string uname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var names = context.Group_Table.Where(g => g.username == uname).ToList();

                return names;
            }
        }
    }
}