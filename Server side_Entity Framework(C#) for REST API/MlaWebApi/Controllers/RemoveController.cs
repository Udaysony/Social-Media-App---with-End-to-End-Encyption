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
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    var gt_ = context.Group_Table.Single(t => t.groupname == gname
                                                                        && t.groupid == gid
                                                                        && t.username == username);

                    context.Group_Table.Remove(gt_);

                    var gk_ = context.Group_Key_Table.Single(t => t.groupid == gid
                                                                   && t.username == username);

                    context.Group_Key_Table.Remove(gk_);

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
                    return e.ToString();
                }
            }
        }
    }
}