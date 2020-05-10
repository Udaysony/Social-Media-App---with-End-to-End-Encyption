using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class Group_Invitation_TableController : ApiController
    {
        [HttpPost]
        public string addRequest(Group_Invitation_Table data)
        {
            string status = "failed";
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var already_exists = context.Group_Invitation_Table.SingleOrDefault(u => u.username_from == data.username_from
                                                                              && u.groupid == data.groupid);

                if (already_exists is null)
                {

                    try
                    {

                        if (data == null)
                        {
                            return "its null";
                        }
                        context.Group_Invitation_Table.Add(data);
                        if (context.SaveChanges() > 0)
                        {
                            status = "SENT";
                            return status;
                        }
                        else
                        {
                            status = "failed in save";
                            return status;
                        }
                    }
                    catch (Exception e)
                    {
                        status = e.ToString();
                        return status;
                    }
                }
                else
                {
                    return "SENT";
                }
            }

        }

        [HttpGet]
        public string RemoveRequest(string owner, string new_user, string groupname)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                    var rawToRemove = context.Group_Invitation_Table.Single(i => i.username_to == owner
                                            && i.username_from == new_user && i.groupname == groupname);

                    context.Group_Invitation_Table.Remove(rawToRemove);

                    if (context.SaveChanges() < 0)
                    {
                        return "ok";
                    }
                    else
                    {
                        return "failed in Save Context";
                    }
                }catch(Exception e)
                {
                    return e.ToString();
                }
                
            }
        }

        [HttpGet]
        public List<Group_Invitation_Table> GetFriendsRequest(string username_to)
        {
            using(MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var invites = context.Group_Invitation_Table.Where(i => i.username_to == username_to && i.isFriend=="yes").ToList();
                return invites;
            }
        }

        
        [HttpGet]
        public List<Group_Invitation_Table> GetGroupJoinRequests(string username_to)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                var invites = context.Group_Invitation_Table.Where(i => i.username_to == username_to 
                && i.isFriend=="no").ToList();
                return invites;
            }
        }
    }
}