package org.example;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class RandomObjectGenerator {
    private final Random random;
    private final Map<Class<?>, Supplier<?>> generators;

    public RandomObjectGenerator() {
        random = new Random();
        generators = new HashMap<>();
        initializeGenerators();
    }

    private void initializeGenerators() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        generators.put(String.class, () -> random
                .ints(leftLimit,rightLimit+1)
                .limit(targetStringLength)
                .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append)
                .toString());
        generators.put(int.class, random::nextInt);
        generators.put(Integer.class, random::nextInt);
        generators.put(long.class, random::nextLong);
        generators.put(Long.class, random::nextLong);
        generators.put(double.class, random::nextDouble);
        generators.put(Double.class, random::nextDouble);
        generators.put(float.class, random::nextFloat);
        generators.put(Float.class, random::nextFloat);
        generators.put(boolean.class, random::nextBoolean);
        generators.put(Boolean.class, random::nextBoolean);
        generators.put(short.class, () -> (short) random.nextInt(Short.MAX_VALUE + 1));
        generators.put(Short.class, () -> (short) random.nextInt(Short.MAX_VALUE + 1));
        generators.put(byte.class, () -> (byte) random.nextInt(Byte.MAX_VALUE + 1));
        generators.put(Byte.class, () -> (byte) random.nextInt(Short.MAX_VALUE + 1));
        generators.put(char.class, () -> (char) random.nextInt(26)+'a');
        generators.put(Character.class, () -> (char) random.nextInt(26)+'a');
    }

    public <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            fillObject(instance, false);
            return instance;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + clazz.getName(), e);
        }
    }

    public <T> void fill(T object) {
        fillObject(object, true);
    }

    private <T> void fillObject(T object, Boolean fillOnlyNulls){
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if(fillOnlyNulls && field.get(object) != null)
                    continue;
                Object value = generateRandomValueForField(field);
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field value", e);
            }
        }
    }

    private Object generateRandomValueForField(Field field) {
        Class<?> type = field.getType();
        Supplier<?> generator = generators.get(type);
        if(generator != null)
            return generator.get();
        else{
            try{
                return generate(type);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to generate random value for field: " + field.getName(), e);
            }
        }
    }
}
