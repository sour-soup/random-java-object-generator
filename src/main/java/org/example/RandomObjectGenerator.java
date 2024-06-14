package org.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.*;
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
                .ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
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
        generators.put(char.class, () -> (char) random.nextInt(26) + 'a');
        generators.put(Character.class, () -> (char) random.nextInt(26) + 'a');
    }

    public <T> T fillNewObject(Class<T> clazz) {
        try {
            if (clazz.isRecord()) {
                return fillNewRecord(clazz);
            } else {
                T instance = clazz.getDeclaredConstructor().newInstance();
                fillObject(instance, false);
                return instance;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + clazz.getName(), e);
        }
    }

    public <T> void fillExistingObject(T object) {
        fillObject(object, true);
    }

    private <T> void fillObject(T object, Boolean fillOnlyNulls) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (fillOnlyNulls && field.get(object) != null)
                    continue;
                Object value = generateRandomValue(field.getType());
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field value", e);
            }
        }
    }

    private <T> T fillNewRecord(Class<T> clazz) {
        try {

            RecordComponent[] components = clazz.getRecordComponents();
            Object[] values = new Object[components.length];
            for (int i = 0; i < components.length; i++)
                values[i] = generateRandomValue(components[i].getType());
            Constructor<T> constructor = clazz.getDeclaredConstructor(Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class<?>[]::new));
            return constructor.newInstance(values);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to create new record instance of " + clazz.getName(), e);
        }
    }

    private Object generateRandomValue(Class<?> type) {
        Supplier<?> generator = generators.get(type);
        if (generator != null)
            return generator.get();
        else {
            try {
                return fillNewObject(type);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate random value of: " + type, e);
            }
        }
    }
}
