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

namespace TestOData.Controllers
{
    public class ProductsController : ODataController
    {
        // GET odata/Products
        [EnableQuery]
        public IQueryable<Product> GetProducts()
        {
            return Repository.Instance.GetProducts().AsQueryable();
        }
    }
}
