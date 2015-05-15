using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    public abstract class ReflectiveEquals : IEquatable<ReflectiveEquals>
    {
        public override bool Equals(object obj)
        {
            if (obj == null) return false;

            var type = this.GetType();
            if (obj.GetType() != type) return false;

            foreach (var propertyInfo in type.GetProperties(BindingFlags.Public | BindingFlags.Instance))
            {
                var property = type.GetProperty(propertyInfo.Name);
                var a = property.GetValue(this);
                var b = property.GetValue(obj);

                if (a == null || b == null)
                {
                    if (a == null && b != null) return false;
                    else if (a != null && b == null) return false;
                }
                else if (a.GetType().IsArray)
                {
                    Array arrayA = a as Array;
                    Array arrayB = b as Array;
                    if (arrayA.Length != arrayB.Length) return false;
                    for (int i = 0; i < arrayA.Length; i++)
                        if (!arrayA.GetValue(i).Equals(arrayB.GetValue(i))) return false;
                }
                else if (!a.Equals(b)) return false;
            }

            return true;
        }

        bool IEquatable<ReflectiveEquals>.Equals(ReflectiveEquals other)
        {
            return ((ReflectiveEquals)this).Equals(other);
        }
    }
}
