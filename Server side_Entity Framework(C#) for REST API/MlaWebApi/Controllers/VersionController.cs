using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using MlaWebApi.Models;

namespace MlaWebApi.Controllers
{
    public class VersionController : ApiController
    {

        [HttpGet]
        public IQueryable GetStatusAndDetails(int gid) {
        
            using( MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    var stat = context.Group_Status_Table.Single(g => g.groupid == gid);

                    if (stat.status == 1)
                    {
                        int? maxV = (int?)context.Group_Key_Table.Where(g => g.groupid == gid).Max(g => (int?)g.version_num);
                        var users = context.Group_Key_Table.Where(u => u.groupid == gid && u.version_num==maxV).ToList();


                        var user_details_ = from u in users
                                            from user in context.Users.ToList()
                                            where u.username == user.username
                                            select new
                                            {
                                                username = u.username,
                                                publicKey = user.publickey
                                            };

                        var result = user_details_.ToList();
                        return result.AsQueryable();
                    }
                    else
                    {
                        return null;
                    }
                }catch(Exception e)
                {
                    return null;
                }
                    
            }
                    
        }

        [HttpGet]
        public int? GetMaxVersion(int gid)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                int? maxV = (int?)context.Group_Key_Table.Where(g => g.groupid == gid).Max(g  => (int?)g.version_num);

                    return maxV;
                

            }
        }
        [HttpPost]
        public String UpdateStatustoClean(int gid)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    Group_Status_Table new_status = new Group_Status_Table { };

                    new_status.groupid = gid;
                    context.Group_Status_Table.Attach(new_status);
                    new_status.status = 0;
                    context.Entry(new_status).Property(n => n.status).IsModified = true;

                    var st = "fail";

                    if (context.SaveChanges() > 0)
                    {
                        return "ok";
                    }
                    else
                    {
                        return st;
                    }
                }
                catch(Exception e)
                {
                    return e.ToString();
                }
            }
        }

        [HttpPost]
        public String AddNewKeyData(string username, string gid, string new_key, string vn)
        {

            int group_id_int = int.Parse(gid);
            int version_no_int = int.Parse(vn);
            using (MlaDatabaseEntities context = new MlaDatabaseEntities()) {

                try
                {
                    Group_Key_Table new_data = new Group_Key_Table
                    {
                        username = username,
                        groupKey = new_key,
                        groupid = group_id_int,
                        version_num = version_no_int

                    };

                    context.Group_Key_Table.Add(new_data);

                    if (context.SaveChanges() > 0)
                    {
                        return "ok";
                    }
                    else
                    {
                        return "add failed";
                    }
                }catch(Exception e)
                {
                    return e.ToString();
                }

            }

        }
    }

}