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
    
    public partial class Group_Invitation_Table
    {
        public int id { get; set; }
        public string username_from { get; set; }
        public string username_to { get; set; }
        public int groupid { get; set; }
        public string groupname { get; set; }
        public string isFriend { get; set; }
    
        public  Group_Status_Table Group_Status_Table { get; set; }
        public User User { get; set; }
        public  User User1 { get; set; }
    }
}
