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
    
    public partial class Group_Status_Table
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public Group_Status_Table()
        {
            this.Group_Invitation_Table = new HashSet<Group_Invitation_Table>();
            this.Group_Key_Table = new HashSet<Group_Key_Table>();
            this.Group_Table = new HashSet<Group_Table>();
            this.Post_Table = new HashSet<Post_Table>();
        }
    
        public int groupid { get; set; }
        public int status { get; set; }
        public string groupname { get; set; }
    
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public  ICollection<Group_Invitation_Table> Group_Invitation_Table { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public  ICollection<Group_Key_Table> Group_Key_Table { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public  ICollection<Group_Table> Group_Table { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public  ICollection<Post_Table> Post_Table { get; set; }
    }
}
