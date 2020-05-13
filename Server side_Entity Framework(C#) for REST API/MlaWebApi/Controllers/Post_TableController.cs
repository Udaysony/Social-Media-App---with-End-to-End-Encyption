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

        [HttpGet]
        public IQueryable GetMyPosts(string username)
        {
            using (MlaDatabaseEntities context = new MlaDatabaseEntities())
            {

                var query =
                    from posts in context.Post_Table
                    from groups in context.Group_Table.Where(t => t.username == username)
                    where groups.groupid == posts.groupid select posts;

                var result = query.ToList();

                var public_post = context.Post_Table.Where(p => p.privacy == "Public" && p.ownerusername != username).ToList();

                var final_result = result.Union(public_post).OrderByDescending(g => g.timestamp);

                return final_result.AsQueryable();
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