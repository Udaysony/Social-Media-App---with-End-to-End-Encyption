using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class Group_Key_TableController : ApiController
    {
        [HttpPost]

        public string AcceptRequest(Group_Key_Table group_key_table, string owner)
        {
            string query_result = "failed";
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    context.Group_Key_Table.Add(group_key_table);

                    Group_Table group_table = new Group_Table
                    {
                        username = group_key_table.username,
                        groupid = group_key_table.groupid,
                        groupname = owner + "Friends",
                        isOwner = "no",
                        isFriend = "yes"
                    };
                    context.Group_Table.Add(group_table);

 
                        var rawToRemove = context.Group_Invitation_Table.Single(t => t.username_from == group_key_table.username
                                    && t.username_to == owner && t.groupid == group_key_table.groupid);

                        context.Group_Invitation_Table.Remove(rawToRemove);
   
                    if (context.SaveChanges() > 0)
                    {
                        query_result = "ok";
                        return query_result;

                    }
                    else
                    {
                        return query_result;
                    }
                }
                catch (Exception e)
                {
                    query_result = e.ToString();
                    return query_result;
                }
            }
        }


        [HttpPost]

        public string AcceptGroupRequest(Group_Key_Table group_key_table, string gname, string owner)
        {
            string query_result = "failed";
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                string s = "";
                try
                {
                    context.Group_Key_Table.Add(group_key_table);
                    s += "group_key_added ";

                    Group_Table group_table = new Group_Table
                    {
                        username = group_key_table.username,
                        groupid = group_key_table.groupid,
                        groupname = gname,
                        isOwner = "no",
                        isFriend = "no"
                    };
                    context.Group_Table.Add(group_table);
                    s += "group_table_added ";

                        var rawToRemove = context.Group_Invitation_Table.Single(t => t.username_from == group_key_table.username
                                    && t.username_to == owner 
                                    && t.groupid == group_key_table.groupid);

                        context.Group_Invitation_Table.Remove(rawToRemove);
                        s += "removed_raw ";

                    if (context.SaveChanges() > 0)
                    {
                        query_result = "ok";
                        return query_result;

                    }
                    else
                    {
                        return s+query_result;
                    }
                }
                catch (Exception e)
                {
                    query_result = s+  e.ToString();
                    return query_result;
                }
            }

        }

            [HttpGet]
            public string GetGroupKey(int gid, string owner)
            {
            string gk = "";

                using (MlaDatabaseEntities context = new MlaDatabaseEntities())
                {

                    var k1 = context.Group_Key_Table.Single(u => u.groupid == gid && u.username == owner);
                    gk = k1.groupKey;
                    return gk;

                }
            }
    }
}