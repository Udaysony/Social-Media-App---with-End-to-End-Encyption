using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class Group_Status_TableController : ApiController
    {
        [HttpPost]
        public String PostGroupStatus(Group_Status_Table group_status_table)
        {
            string status;
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    context.Group_Status_Table.Add(group_status_table);
                    if (context.SaveChanges() > 0)
                    {
                        status = group_status_table.groupid.ToString();
                        return status;
                    }
                    else
                    {
                        status = "failed";
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


        [HttpGet]
        public List<Group_Status_Table> getAGroup(int id)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Status_Table.Where(g => g.groupid == id).ToList();
                return gps;
            }
        }

        [HttpGet]
        public Group_Status_Table GetGroupInfo(string groupname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Status_Table.Single(g => g.groupname == groupname);
                return gps;
            }
        }
        
        [HttpGet]
        public List<Group_Status_Table> GetAllGroupInfo(string groupname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var gps = context.Group_Status_Table.ToList();
                return gps;
            }
        }
    }
    }