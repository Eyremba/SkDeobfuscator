package com.gmail.gotofinaldev.skdeobf;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Main
{
    static final File logs = new File("logs.txt");
    static PrintStream ps;

    static final Pattern  DATA_SPLIT = Pattern.compile(" ");
    static final int      WARN_POWER = 1000;
    static final int      PAUSE_TIME = 10000;
    static final String   BAD_CHAR_1 = ((char) 197) + "" + ((char) 8533);
    static final String   BAD_CHAR_2 = ((char) 196) + "" + ((char) 8516);
    static final String   BAD_CHAR_3 = ((char) 196) + "" + ((char) 8498);
    static final String   BAD_CHAR_4 = ((char) 314) + "" + ((char) 8267);
    static final String   BAD_CHAR_5 = ((char) 259) + "" + ((char) 323);
    static final String   BAD_CHAR_6 = ((char) 313) + "" + ((char) 8234);
    static final String[] BAD_CHARS  = {BAD_CHAR_1, BAD_CHAR_2, BAD_CHAR_3, BAD_CHAR_4, BAD_CHAR_5, BAD_CHAR_6};

    static File inFile = new File("in.txt");
    static File outDir = new File("out");

    static boolean checkBadChars = true;

    static void exception(final Exception e)
    {
        e.printStackTrace(ps);
        e.printStackTrace();
    }

    static void log(final Object str)
    {
        ps.println(str);
        ps.flush();
        System.out.println(str);
    }

    public static void main(final String[] args) throws InterruptedException, FileNotFoundException
    {
        try
        {
            if (! logs.exists())
            {
                logs.createNewFile();
            }
            ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(logs)));
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
        log("============== Sk Deobfuscator ==============");
        if ((args.length == 1) && args[0].equalsIgnoreCase("help"))
        {
            log("Po prsotu obok programu zrób plik in.txt, a w nim, linia po lini:\n  <siła z jaką był zaciemniany> <scieżka do pliku>\n  siłę możesz znaleźć w skrypcie ładującym ten plik, jeśli jednak nie masz takiego pliku, więc nawet nie masz jak odpalic tego skryptu, to zastąp siłę znakiem zapytania a program postara się sam znaleźć tę wartość.\n  Przykład: (scieżka może byc relatywna do pliku, nie musisz dawać całej, razem z dyskiem itd)\n34 skrypty/plik.txt\n  ? C:/Users/Admin/Downloads/plik.txt");
            pause();
            return;
        }
        int arg = 0;
        if ((args.length >= (arg + 1)) && args[arg].equalsIgnoreCase("-nobad"))
        {
            arg++;
            checkBadChars = false;
        }
        if (args.length >= (arg + 1))
        {
            inFile = new File(args[arg++]);
        }
        if (args.length >= (arg + 1))
        {
            outDir = new File(args[arg]);
        }
        if (! outDir.exists())
        {
            outDir.mkdirs();
        }
        if ((inFile == null) || ! inFile.exists())
        {
            log("[Error] Plik z danymi wejściowymi nie istnieje, ścieżka: " + (inFile != null ? inFile.getAbsolutePath() : "<brak scieżki pliku>"));
            pause();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(inFile)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    final String[] data = DATA_SPLIT.split(line, 2);
                    tryDecode(new File(data[1]), data[0].equals("?") ? 0 : Integer.parseInt(data[0]));
                } catch (final Exception e)
                {
                    log("[Error] Nie udało się odczytać pliku: \"" + line + "\", z powodu: " + e.getMessage());
                    exception(e);
                    pause();
                    return;
                }
            }
        } catch (final Exception e)
        {
            exception(e);
            pause();
        }
    }

    static boolean hasBadChar(final String str)
    {
        if (! checkBadChars)
        {
            return false;
        }
        for (final String bad : BAD_CHARS)
        {
            if (str.contains(bad))
            {
                return true;
            }
        }
        return false;
    }

    static void tryDecode(final File file, long power) throws Exception
    {
        final String code;
        log("[Info] Rozkodywanie pliku: \"" + file.getPath() + "\" o mocy: " + ((power == 0) ? "? (nieznana)" : power));
        if (power != 0)
        {
            code = decode(file, power);
            if (isProtected(code))
            {
                log("[Warn] Dekodowanie pliku \"" + file.getPath() + "\" z podaną mocą: " + power + ", nie udało się, program rozpoczyna szukanie mocy...");
                tryDecode(file, 0);
                return;
            }
        }
        else
        {
            final String protectedCode = unzip(file);
            String tempCode;
            while (isProtected(tempCode = decode(protectedCode, ++ power)))
            {
                if ((power % WARN_POWER) == 0)
                {
                    log("[Warn] Sprawdzono już  " + power + " mozliwości i dalej nie udało się znaleźć rozwiązania! (skanuje dalej...)");
                }
            }
            code = tempCode;
        }
        saveResult(file, code, power);
    }

    static void pause() throws InterruptedException
    {
        Thread.sleep(PAUSE_TIME);
    }

    static void saveResult(final File file, final String code, final long power) throws Exception
    {
        final String fileName = file.getName();

        int temp = 1;
        final String outFileName = fileName.endsWith("txt") ? fileName.replace(".txt", ".sk") : (fileName + ".sk");
        File outFile = new File(outDir, outFileName);
        while (outFile.exists())
        {
            outFile = new File(outDir, outFileName + "-" + (temp++));
        }
        outFile.createNewFile();
        try (FileWriter out = new FileWriter(outFile, false))
        {
            out.write(code);
            out.flush();
        }
        log("[Info] Rozkodowano plik: \"" + file.getPath() + "\" do: \"" + outFile.getPath() + "\", moc: " + power);
    }

    static String unzip(final File zip) throws Exception
    {
        final String code;
        try (final ZipFile zipFile = new ZipFile(zip))
        {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            InputStream is = null;
            while (entries.hasMoreElements())
            {
                final ZipEntry entry = entries.nextElement();
                is = zipFile.getInputStream(entry);
            }
            if (is == null)
            {
                return null;
            }
            try (Scanner scanner = new Scanner(is, "UTF-8"))
            {
                code = scanner.useDelimiter("\\A").next();
            }
            is.close();
        }
        return code;
    }

    static boolean isProtected(final String code)
    {
        if ((code.contains("\t") && code.contains(" ") && (code.contains("stop") || code.contains("send") || code.contains("else"))))
        {
            final String[] strings = code.split("\n");
            for (final String str : strings)
            {
                if (str.endsWith("~") || str.endsWith("~:") || hasBadChar(str))
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    static String decode(final String coded, final long power) throws Exception
    {
        if (coded == null)
        {
            return null;
        }
        final String[] values = coded.split("-");
        final StringBuilder code = new StringBuilder("");
        for (final String un : values)
        {
            if ((un != null) && (! un.isEmpty()))
            {
                long i = Long.valueOf(un);
                i /= power;
                code.append((char) i);
            }
        }
        return code.toString();
    }

    static String decode(final File file, final long power) throws Exception
    {
        return decode(unzip(file), power);
    }
}
