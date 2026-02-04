package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.LinkedList;
import java.util.List;

public class ListValue extends GearshiftValue {
   private LinkedList<GearshiftValue> value;

   public ListValue(LinkedList<GearshiftValue> value) {
       this.value = value;
       declareField("add", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.add", args, 1, file, line);
               value.add(args.getFirst());
               return new NullValue();
           }
       });
       declareField("addAll", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.addAll", args, 1, file, line);
               value.addAll(((ListValue) args.getFirst()).value);
               return new NullValue();
           }
       });
       declareField("addFirst", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.addFirst", args, 1, file, line);
               value.addFirst(args.getFirst());
               return new NullValue();
           }
       });
       declareField("addFirstAll", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.addFirstAll", args, 1, file, line);
               for (var element : ((ListValue) args.getFirst()).value)
                   value.addFirst(element);
               return new NullValue();
           }
       });
       declareField("__get_index", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.__get_index", args, 1, file, line);
               return value.get((int) ((NumberValue) args.getFirst()).checkDouble());
           }
       });
       declareField("__assign_index", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.__assign_index", args, 2, file, line);
               value.set((int) ((NumberValue) args.getFirst()).checkDouble(), args.get(1));
               return new NullValue();
           }
       });
       declareField("contains", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.contains", args, 1, file, line);
               for (var element : value) {
                   if (((BooleanValue) element.accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue()) {
                       return new BooleanValue(true);
                   }
               }
               return new BooleanValue(false);
           }
       });
       declareField("size", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.size", args, 0, file, line);
               return new NumberValue(value.size());
           }
       });
       declareField("isEmpty", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.isEmpty", args, 0, file, line);
               return new BooleanValue(value.isEmpty());
           }
       });
       declareField("clear", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.clear", args, 0, file, line);
               value.clear();
               return new NullValue();
           }
       });
       declareField("remove", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.remove", args, 1, file, line);
               for (int i = 0; i < value.size(); i++) {
                   if (((BooleanValue) value.get(i).accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue()) {
                       value.remove(i);
                       break;
                   }
               }
               return new NullValue();
           }
       });
       declareField("removeAt", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.removeAt", args, 1, file, line);
               return value.remove((int) ((NumberValue) args.getFirst()).checkDouble());
           }
       });
       declareField("removeFirst", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.removeFirst", args, 0, file, line);
               return value.removeFirst();
           }
       });
       declareField("removeLast", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.removeLast", args, 0, file, line);
               return value.removeLast();
           }
       });
       declareField("indexOf", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.indexOf", args, 1, file, line);
               for (var i = 0; i < value.size(); i++) {
                   if (((BooleanValue) value.get(i).accessField("__eq", file, line).call(List.of(args.getFirst()), file, line)).getValue()) {
                       return new NumberValue(i);
                   }
               }
               return new NullValue();
           }
       });
       declareField("__iterator", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.__iterator", args, 0, file, line);
               return new FunctionValue() {
                   int currentIndex = 0;
                   @Override
                   public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                       checkArgs("List.__iterator()", args, 0, file, line);
                       if (currentIndex >= value.size()) {
                           return new NullValue();
                       } else {
                           var result = value.get(currentIndex);
                           currentIndex++;
                           return result;
                       }
                   }
               };
           }
       });
       declareField("__tostring", new FunctionValue() {
           @Override
           public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
               checkArgs("List.__tostring", args, 0, file, line);
               var sb = new StringBuilder();
               sb.append('[');
               for (var element : value) {
                   sb.append(element.tojstring(file, line));
                   sb.append(", ");
               }
               if (sb.toString().length() > 1) {
                   sb.deleteCharAt(sb.length() - 1);
                   sb.deleteCharAt(sb.length() - 1);
               }
               sb.append(']');
               return new StringValue(sb.toString());
           }
       });
   }
    public LinkedList<GearshiftValue> checkList() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "List";
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
