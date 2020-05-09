using MlaWebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace MlaWebApi.Controllers
{
    public class Post_TableController : ApiController
    {

        [HttpGet]
        public List<Post_Table> GetAllPosts()
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                var posts = context.Post_Table.ToList();
                return posts;
            }
        }

        [HttpPost]
        public string uploadPost(Post_Table post_table)
        {
            string query_status = "failed";
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {
                try
                {
                   
                    context.Post_Table.Add(post_table);
                    if (context.SaveChanges() > 0)
                    {
                        query_status = "ok";
                        return query_status;
                    }
                    else
                    {
                        return query_status;
                    }
                }
                catch(Exception e)
                {
                    query_status = "failed"+e.ToString();
                    return query_status;
                }
                
            }
        }
    }
}