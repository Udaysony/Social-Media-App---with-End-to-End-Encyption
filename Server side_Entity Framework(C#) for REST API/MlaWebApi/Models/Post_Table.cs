//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MlaWebApi.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class Post_Table
    {
        public int postid { get; set; }
        public string post { get; set; }
        public string sessionKey { get; set; }
        public string digitalSignature { get; set; }
        public int groupid { get; set; }
        public int version_num { get; set; }
        public string privacy { get; set; }
        public string ownerusername { get; set; }
        public Nullable<int> originalpostid { get; set; }
        public System.DateTime timestamp { get; set; }
    
        public  Group_Status_Table Group_Status_Table { get; set; }
        public  User User { get; set; }
    }
}
