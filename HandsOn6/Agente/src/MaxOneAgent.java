import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.Random;

public class MaxOneAgent extends Agent {

    protected void setup() {
        System.out.println("Agent "+getLocalName()+" started.");
        addBehaviour(new MyOneShotBehaviour());
    }

    private static int tamPoblacion = 6, Generacion=0;
    private static ArrayList<Individuo> poblacion;
    private static ArrayList<Individuo> hijos;

    private static boolean banderaEs1023 = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static class Individuo
    {
        private long Genoma;
        private float Fitness;
        private float Probabilidad;
        private float ProbabilidadAcumulada;

        public String DecimalToBinario(long num)
        {
            if(num<0) //absoluto
            {
                num = num * -1;
            }
            long n = num;
            String bin = "";

            if(n == 0)
            {
                return "0000000000";
            }

            while(n>0)
            {
                if(n%2 == 0)
                    bin = "0" + bin;
                else
                    bin = "1" + bin;
                n = n/2;
            }

            while (bin.length() < 10)
            {
                bin = "0" + bin;
            }

            return bin;
        }

        public long BinarioToDecimal(String binario)
        {
            long decimal = 0;
            int posicion = 0;
            for (int x = binario.length() - 1; x >= 0; x--) {
                short digito = 1;
                if (binario.charAt(x) == '0') {
                    digito = 0;
                }
                double multiplicador = Math.pow(2, posicion);
                decimal += digito * multiplicador;
                posicion++;
            }
            return decimal;
        }
    }

    public static int ObtenerRandomEntero(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void CrearPrimeraPoblacion()
    {
        poblacion = new ArrayList<>();
        hijos = new ArrayList<>();
        for (int i = 0; i < tamPoblacion; i++)
        {
            Individuo ind = new Individuo();
            ind.Genoma = ObtenerRandomEntero(1, 255);
            poblacion.add(ind);
        }
    }

    public static void MetodoBurbuja(){
        boolean sw = false;
        while (!sw)
        {
            sw = true;
            for (int i = 1; i < poblacion.size(); i++)
            {
                if (poblacion.get(i).Fitness > poblacion.get(i - 1).Fitness)
                {
                    Individuo ind = poblacion.get(i);
                    poblacion.set(i, poblacion.get(i - 1));
                    poblacion.set(i - 1, ind);
                    sw = false;
                }
            }
        }
    }

    static double FuncionFitness(float x)
    {
        return x/1023;
    }

    public static void DeterminarFitness(){
        for (int i = 0; i < poblacion.size(); i++)
        {
            Individuo ind = poblacion.get(i);
            ind.Fitness = (float) (FuncionFitness(poblacion.get(i).Genoma));
            poblacion.set(i, ind);
        }
    }

    public static void ImprimirValores()
    {
        System.out.println("Generacion no. " + Generacion);
        for (int i = 0; i < tamPoblacion; i++)
        {
            if(poblacion.get(i).Fitness == 1f || banderaEs1023 == true)
                System.out.println("(" + i + ")" + ANSI_GREEN + poblacion.get(i).Genoma + ANSI_RESET + " p:" +  ANSI_GREEN + poblacion.get(i).Fitness + ANSI_RESET);
            else
                System.out.println("(" + i + ")" + poblacion.get(i).Genoma + " p:" + poblacion.get(i).Fitness);
        }
        System.out.println("\n");
        Generacion++;
    }

    static void Combinacion()
    {
        float puntaje, puntajeAcum = 0, puntajeAcumInd = 0, buff=0;
        for(int i = 0; i < poblacion.size(); i++)
        {
            puntaje = poblacion.get(i).Fitness;
            puntajeAcum = puntajeAcum + puntaje;
        }
        for(int i = 0; i < poblacion.size(); i++)
        {
            Individuo ind = poblacion.get(i);
            puntaje = poblacion.get(i).Fitness;
            puntajeAcumInd = puntaje/puntajeAcum;
            ind.Probabilidad = puntajeAcumInd;
            buff = puntajeAcumInd + buff;
            ind.ProbabilidadAcumulada = buff;
            poblacion.set(i,ind);
        }
        Random r = new Random();
        float randRuletaNum = 0 + r.nextFloat() * (poblacion.get(poblacion.size()-1).ProbabilidadAcumulada - 0);
        float randRuletaNum2 = 0 + r.nextFloat() * (poblacion.get(poblacion.size()-1).ProbabilidadAcumulada - 0);
        float bufferRuleta = 0, probabilidadActual;
        long temporal;
        String Padre1 = "0000000000";
        String Padre2 = "0000000000";
        boolean Padre1Listo = false;
        boolean Padre2Listo = false;
        for(int i = 0; i < poblacion.size(); i++)
        {
            Individuo ind = poblacion.get(i);
            probabilidadActual = poblacion.get(i).ProbabilidadAcumulada;
            if((bufferRuleta<randRuletaNum && randRuletaNum<=probabilidadActual) && Padre1Listo == false)
            {
                temporal = poblacion.get(i).Genoma;
                Padre1 = ind.DecimalToBinario(temporal);
                Padre1Listo = true;
            }
            else
            {
                if((bufferRuleta<randRuletaNum2 && randRuletaNum2<=probabilidadActual) && Padre2Listo == false)
                {
                    temporal = poblacion.get(i).Genoma;
                    Padre2 = ind.DecimalToBinario(temporal);
                    Padre2Listo = true;
                }
                else {
                    bufferRuleta = probabilidadActual;
                }
            }
        }

        int mutacion = ObtenerRandomEntero(0,Padre1.length());
        int contador;
        StringBuilder Hijo1 = new StringBuilder(Padre1.length());
        StringBuilder Hijo2 = new StringBuilder(Padre1.length());

        for(contador = 0; contador < mutacion; contador++)
        {
            Hijo1.append(Padre2.charAt(contador));
            Hijo2.append(Padre1.charAt(contador));
        }
        for(int cont2 = contador; cont2 < Padre1.length(); cont2++)
        {
            Hijo1.append(Padre1.charAt(cont2));
            try {
                Hijo2.append(Padre2.charAt(cont2));
            }
            catch(Exception e){
                System.out.println("Hijo1: " + Hijo1);
                System.out.println("Hijo1 TAM: " + Hijo1.length());
                System.out.println("Hijo2: " + Hijo2);
                System.out.println("Hijo2 TAM: " + Hijo2.length());
            }
        }
        Individuo ind = new Individuo();
        ind.Genoma = ind.BinarioToDecimal(String.valueOf(Hijo1));
        hijos.add(ind);
        Individuo ind2 = new Individuo();
        ind2.Genoma = ind2.BinarioToDecimal(String.valueOf(Hijo2));
        hijos.add(ind2);
    }

    static void Mutacion(){
        long GenomaHijo;
        StringBuilder SGenoma;
        float ratioMutacion = 0.1f;
        Random r = new Random();
        for(int i=0; i< hijos.size(); i++)
        {
            float randRatioMutacion = 0 + r.nextFloat() * (1 - 0);
            if(randRatioMutacion < ratioMutacion)
            {
                Individuo ind = hijos.get(i);
                GenomaHijo = hijos.get(i).Genoma;
                SGenoma = new StringBuilder(ind.DecimalToBinario(GenomaHijo));
                int rand = ObtenerRandomEntero(0, SGenoma.length());
                char bit = SGenoma.charAt(rand);
                if (bit == '1') {
                    SGenoma.setCharAt(rand, '0');
                } else {
                    SGenoma.setCharAt(rand, '1');
                }
                ind.Genoma = ind.BinarioToDecimal(String.valueOf(SGenoma)); //B0 255
            }
        }
    }

    static void ActualizarGeneracion()
    {
        poblacion = new ArrayList<>();
        for(int i = 0; i < hijos.size(); i++)
        {
            poblacion.add(hijos.get(i));
        }
        hijos = new ArrayList<>();
    }

    private class MyOneShotBehaviour extends OneShotBehaviour
    {

        public void action()
        {
            CrearPrimeraPoblacion();
            int ite = 0;
            while(banderaEs1023!=true)
            {
                DeterminarFitness();
                ImprimirValores();
                int j =0;
                while(j < poblacion.size())
                {
                    if(poblacion.get(j).Fitness == 1.0f)
                    {
                        banderaEs1023 = true;
                        break;
                    }
                    j++;
                }
                while (hijos.size() < poblacion.size()) {
                    Combinacion();
                }
                Mutacion();
                ActualizarGeneracion();
                ite++;
            }
            System.out.println("\nNumero Final de Iteraciones: " + ite++);
        }

        public int onEnd()
        {
            myAgent.doDelete();
            return super.onEnd();
        }
    }    // END of inner class ...Behaviour
}
