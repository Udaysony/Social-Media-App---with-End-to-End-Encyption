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
                        return s + query_result;
                    }
                }
                catch (Exception e)
                {
                    query_result = s + e.ToString();
                    return query_result;
                }
            }

        }

        [HttpGet]
        public string GetGroupKey(int gid, int vn, string owner)
        {
            string gk = "";

            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                if (vn == 0)
                {
                    var raws = context.Group_Key_Table.Where(g => g.groupid == gid && g.username == owner).ToList();

                    int Maxvn = raws.Max(r => r.version_num);

                    var k2 = context.Group_Key_Table.Single(u => u.groupid == gid && u.version_num == Maxvn && u.username == owner);
                    gk = k2.groupKey;
                    return gk;
                }
                else
                {

                    var k1 = context.Group_Key_Table.Single(u => u.groupid == gid && u.version_num == vn && u.username == owner);
                    gk = k1.groupKey;
                    return gk;

                }
            }
        }


        [HttpGet]
        public IQueryable GetMyGroupKey(string owner)
        {
            //string gk = "";

            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gid_war = context.Group_Status_Table.SingleOrDefault(s => s.groupname == owner + "Friends");

                int gid = gid_war.groupid;

                var raws = context.Group_Key_Table.Where(g => g.groupid == gid && g.username == owner).ToList();

                int Maxvn = raws.Max(r => r.version_num);

                var k2 = context.Group_Key_Table.Where(u => u.groupid == gid  && u.version_num == Maxvn
                                                &&  u.username == owner).
                    Select(u => new { 
                    groupid = u.groupid,
                    version_num = u.version_num,
                    groupKey = u.groupKey
                    }).ToList();

                //gk = k2.groupKey;
                return k2.AsQueryable();
            }
        }
    }

}