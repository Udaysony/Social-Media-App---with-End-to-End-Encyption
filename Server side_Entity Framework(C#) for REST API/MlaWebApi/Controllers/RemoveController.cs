using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using MlaWebApi.Models;

namespace MlaWebApi.Controllers
{
    public class RemoveController : ApiController
    {
        [HttpGet]

        public string RemoveGroup(int gid, string gname, string username)
        {
            string check_ = "";
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                var f = context.Group_Table.Single(g => g.groupid == gid && g.username == username);

                if (f.isOwner == "yes") return "Can not Remove Owner";

                check_ += "Ownership checked ";
                try
                {

                    var gt_ = context.Group_Table.Single(t => t.groupname == gname
                                                                && t.groupid == gid
                                                                && t.username == username);

                    string grp_name = gt_.groupname;
                    context.Group_Table.Remove(gt_);
                    check_ += " gt removed ";



                    int? maxV = (int?)context.Group_Key_Table.Where(g => g.groupid == gid).Max(g => (int?)g.version_num);

                    var gk_ = context.Group_Key_Table.Single(t => t.groupid == gid
                                                                   && t.username == username && t.version_num == maxV);

                    context.Group_Key_Table.Remove(gk_);
                    check_ += "gkt removed ";


                    //update status
                    Group_Status_Table new_status = new Group_Status_Table { };

                    new_status.groupid = gid;
                    context.Group_Status_Table.Attach(new_status);
                    new_status.status = 1;
                    context.Entry(new_status).Property(n => n.status).IsModified = true;
                    check_ += " status checked";



                    if (context.SaveChanges() > 0)
                    {
                        return "removed";
                    }
                    else
                    {
                        return "failed";
                    }


                }
                catch(Exception e)
                {
                    return  check_ + e.ToString();
                }
            }
        }
    }
}