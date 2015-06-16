using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Dynamic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.GeneralUtilities
{
    public static class Utils
    {
        private static Random random = new Random((int)DateTime.Now.Ticks);

        public static string BuildRandomString(int size)
        {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < size; i++)
            {
                ch = Convert.ToChar(Convert.ToInt32(Math.Floor(26 * random.NextDouble() + 97)));
                builder.Append(ch);
            }

            return builder.ToString();
        }

        public static dynamic AddProperty(this object obj, string newPropertyName, object newPropertyValue)
        {
            var type = obj.GetType();
            var newObj = new ExpandoObject() as IDictionary<string, object>;

            foreach (PropertyDescriptor property in TypeDescriptor.GetProperties(obj.GetType()))
                newObj.Add(property.Name, property.GetValue(obj));

            newObj.Add(newPropertyName, newPropertyValue);

            return newObj as ExpandoObject;
        }
    }
}
