package Software.SistemaOperacional;

import java.util.ArrayList;

import Hardware.Memory;
import Hardware.Memory.Word;

public class MemoryManager{

        protected Memory memory;
        private int pageLength;
        private int frameLength;
        private int nFrames;
        private boolean freeFrames[];

        public MemoryManager(Memory _m, int _pLength){
            memory = _m;
            pageLength = _pLength;
            frameLength = pageLength;
            nFrames = memory.address.length/pageLength;

            freeFrames = new boolean[nFrames];
            for(int i = 0; i < freeFrames.length; i++){

                freeFrames[i] = true;
                //#region furos
                if(i == 1)
                {
                    freeFrames[i] = false;
                }

                if(i == 5)
                {
                    freeFrames[i] = false;
                }
                
                if(i == 7)
                {
                    freeFrames[i] = false;
                }

                if(i == 9)
                {
                    freeFrames[i] = false;
                }
                //#endregion

            }
        }

        public class AllocatesReturn{
            boolean canAlocate;
            int[] tablePages; //Frames que o programa será armazenado.
        }

        //Verifica se é possível alocar um programa com "n" instruções na memória atual.
        public AllocatesReturn allocates(int nWords){
            AllocatesReturn allocatesReturn = new AllocatesReturn();
            int nrFrames; //Numero de frames para alocar o programa.
            ArrayList<Integer> aux = new ArrayList<Integer>(); //Responsável por armazenar quais frames foram alocados para o programa.


            //Verificando o numero de frames necessários para o programa.
            if (nWords % pageLength > 0){ // Com offset
                nrFrames = (nWords / pageLength) + 1;
            } else {
                nrFrames = (nWords / pageLength);
            }

            //First Fit => Verifica na lista dos frames quais estão livres e aptos a receber o programa.
            //Array "aux" irá armazenar os frames de memória escolhidos para este programa.
            for(int i = 0; i < freeFrames.length; i++){
                if(nrFrames > 0){
                    if(freeFrames[i]){
                        aux.add(i);
                        freeFrames[i] = false;
                        nrFrames--;
                    }
                } else break;
            }

            //Salvando frames que o programa será armazenado.
            allocatesReturn.tablePages = aux.stream().mapToInt(i -> i).toArray(); //Convertando Wrapper para primitivo.
            
            //O numero de frames é decrementado sempre que uma página pode ser alocada, então se for maior que 0 não foi possível alocar o programa todo na memória.
            if(nrFrames > 0){ 
                deallocate(allocatesReturn.tablePages);
                allocatesReturn.canAlocate = false;
            }else{
                allocatesReturn.canAlocate = true;
            }

            return allocatesReturn;
        }

        //Seta páginas para livres.
        public void deallocate(int[] tablePages){
            for (int page : tablePages) {
                freeFrames[page] = true;
            }
        }

        //Carrega um programa na memórias nas páginas informadas.
        public void carga(Word[] program, int[] tablePages) {
            int countWords = 0; //Numero de Words já alocadas em memoria.

            int countPageSize = 0; //Número de Word's já alocadas no frame.

            //Para cada frame escolhido para alocar o programa, faça:
            for (int frame : tablePages) { // troquei i por frame

                // troquei j por realMemoryPosition
                int realMemoryPosition = frame * frameLength; //Posição na memória onde o frame se inicia.
                while(realMemoryPosition < (frame + 1) * frameLength){ //Posição na memória onde o frame encerra.
                    countPageSize = 0;
                    while (countPageSize < frameLength) {
                        if(countWords < program.length){
                            memory.address[realMemoryPosition] = program[countWords];
                        } 

                        countWords++; //Proxima Word
                        countPageSize++;//Número de Word's já alocadas no frame.
                        realMemoryPosition++; //Percorre as posições na memoria do respectivo frame.
                    }
                }

            }
            
        }




    }
