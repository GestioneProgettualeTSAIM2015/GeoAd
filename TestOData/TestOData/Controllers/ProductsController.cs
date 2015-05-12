using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Query;
using System.Web.Http.OData.Routing;
using Microsoft.Data.OData;
using TestOData.Models;
using Npgsql;

namespace TestOData.Controllers
{
    public class ProductsController : ODataController
    {
        // GET odata/Products
        [EnableQuery]
        public IQueryable<Product> GetProducts()
        {
            int id = RequestContext.Principal.Identity.GetHashCode();

            DataSet ds = new DataSet();
            DataTable dt;
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            NpgsqlConnection connection = new NpgsqlConnection(connectionString);
            connection.Open();
            string query = "SELECT * FROM public.\"Products\"";
            NpgsqlDataAdapter da = new NpgsqlDataAdapter(query, connection);
            ds.Reset();
            da.Fill(ds);
            int tables = ds.Tables.Count;
            dt = ds.Tables[0];

            List<Product> products = new List<Product>(dt.Rows.Count);
            foreach (DataRow dr in dt.Rows)
            {
                products.Add(new Product
                {
                    Id = dr.Field<int>("Id"),
                    CategoryId = dr.Field<int>("CategoryId"),
                    Name = dr.Field<string>("Name")
                });
            }

            return products.AsQueryable();
        }
    }
}
